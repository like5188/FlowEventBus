package com.like.floweventbus

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect

/**
 * 一个[BusObserver]注解的方法中，每个[tag](编译时自动去重了的)对应一个事件。
 * 事件中存在两个流。分别对应事件参数类型为“可空类型”和“非空类型”。
 * 1、如果事件参数类型是“可空类型”，那么[flowNullable]、[flowNotNull]都存在，这两个流发射的消息事件都能接收到。
 * 2、如果事件参数类型是“非空类型”，那么只有[flowNotNull]存在，并且只有它发射的消息事件才能接收到。
 */
@OptIn(DelicateCoroutinesApi::class)
class Event(
    val hostClass: String,// 宿主类
    val tag: String,// 标签
    val requestCode: String,// 请求码。当标签相同时，可以使用请求码区分
    val isSticky: Boolean,
    val paramType: String,// 被@BusObserver注解标注的方法的参数类型。只支持一个参数
    val isNullable: Boolean,
    val callback: (Any, Any?) -> Unit
) {
    private val flowNullable: MutableSharedFlow<Any?>? by lazy {
        if (isNullable) {
            FlowManager.findFlowOrCreateIfAbsent(tag, requestCode, isSticky, paramType, true)
        } else {
            null
        }
    }
    private val flowNotNull: MutableSharedFlow<Any?>? by lazy {
        // 如果参数类型是可空类型，那么需要把它的 flowNotNull 对应的参数变为java基本数据类型，
        // 这样才能和参数类型是非空类型时一致。

        // 比如：
        // 参数类型为非空类型时：
        // flowNotNull ： key=like1--int-false-false

        // 参数类型为可空类型时，转换 paramType 后：
        // flowNotNull ： key=like1--int-false-false
        // 如果不转换：
        // flowNotNull ： key=like1--java.lang.Integer-false-false

        val notNullParamType = paramType.toJavaDataType(false)
        FlowManager.findFlowOrCreateIfAbsent(tag, requestCode, isSticky, notNullParamType, false)
    }
    val hosts = mutableListOf<Any>() // 宿主，同一个hostClass的多个实例
    val owners = mutableListOf<LifecycleOwner?>() // 宿主所属的生命周期类
    private val jobs = mutableListOf<Job>()

    /**
     * 绑定事件到宿主和生命周期类
     */
    fun bind(host: Any, owner: LifecycleOwner?) {
        hosts.add(host)
        owners.add(owner)

        val scope = owner?.lifecycleScope ?: GlobalScope
        scope.launch(Dispatchers.Main) {
            launch {
                flowNullable?.collect {
                    callback(host, it)
                }
            }
            launch {
                flowNotNull?.collect {
                    callback(host, it)
                }
            }
        }.apply {
            jobs.add(this)
            Log.v(TAG, "绑定事件   --> ${this@Event}")
            Log.v(TAG, "宿主      --> $host")
            Log.v(TAG, "生命周期类 --> $owner")
            EventManager.logHostAndOwner()

            invokeOnCompletion {
                Log.i(TAG, "解绑事件 --> ${this@Event}")
                Log.i(TAG, "宿主      --> $host")
                Log.i(TAG, "生命周期类 --> $owner")
                hosts.remove(host)
                owners.remove(owner)
                jobs.remove(this)
                EventManager.logHostAndOwner()
            }
        }
    }

    /**
     * 解绑事件的宿主和生命周期类
     */
    fun unbind(host: Any) {
        val index = hosts.indexOf(host)
        if (index >= 0) {
            jobs[index].cancel()
        }
    }

    fun post(data: Any?, isNullable: Boolean) {
        val scope = owners.firstOrNull()?.lifecycleScope ?: GlobalScope
        scope.launch(Dispatchers.Main) {
            try {
                if (isNullable) {
                    flowNullable?.emit(data)
                } else {
                    flowNotNull?.emit(data!!)
                }
            } catch (e: Exception) {
            }
        }
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("Event(")
        sb.append("hostClass=$hostClass, ")
        sb.append("tag=$tag, ")
        if (requestCode.isNotEmpty()) {
            sb.append("requestCode=$requestCode, ")
        }
        if (isSticky) {
            sb.append("isSticky=$isSticky, ")
        }
        if (paramType != NoArgs::class.java.name) {
            sb.append("param=[")
                .append(paramType)
                .append(", ")
                .append(if (isNullable) "nullable" else "notNull")
                .append("]")
        }
        if (sb.endsWith(", ")) {
            sb.delete(sb.length - 2, sb.length)
        }
        sb.append(")")
        return sb.toString()
    }

}
