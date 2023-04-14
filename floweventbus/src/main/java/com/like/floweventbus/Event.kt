package com.like.floweventbus

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect

/**
 * 一个[BusObserver]注解的方法中，每个[tag](编译时自动去重了的)对应一个事件。
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
    private val flow: MutableSharedFlow<Any?> by lazy {
        FlowManager.findFlowOrCreateIfAbsent(tag, requestCode, isSticky, paramType, isNullable)
    }
    val hosts = mutableListOf<Any>() // 宿主，同一个hostClass的多个实例
    private val jobs = mutableListOf<Job>()

    /**
     * 绑定事件到宿主和生命周期类
     */
    fun bind(host: Any, owner: LifecycleOwner?) {
        hosts.add(host)

        (owner?.lifecycleScope ?: GlobalScope).launch(Dispatchers.Main) {
            launch {
                flow.collect {
                    callback(host, it)
                }
            }
        }.apply {
            jobs.add(this)

            Log.v(TAG, "绑定事件 --> ${this@Event}")
            Log.v(TAG, "宿主   --> $host")
            Log.v(TAG, "生命周期--> $owner")
            EventManager.logHost()

            invokeOnCompletion {
                hosts.remove(host)
                jobs.remove(this)

                Log.i(TAG, "解绑事件 --> ${this@Event}")
                Log.i(TAG, "宿主   --> $host")
                Log.i(TAG, "生命周期--> $owner")
                EventManager.logHost()
            }
        }
    }

    /**
     * 解绑事件的宿主
     */
    fun unbind(host: Any) {
        val index = hosts.indexOf(host)
        if (index >= 0) {
            jobs[index].cancel()
        }
    }

    fun post(data: Any?) {
        GlobalScope.launch(Dispatchers.Main) {
            flow.emit(data)
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
