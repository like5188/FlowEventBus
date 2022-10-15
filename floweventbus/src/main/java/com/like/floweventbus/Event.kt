package com.like.floweventbus

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull

/**
 * 事件中存在两个流。分别对应事件参数类型为“可空类型”和“非空类型”。
 * 1、如果事件参数类型是“可空类型”，那么[flowNullable]、[flowNotNull]都存在，这两个流发射的消息事件都能接收到。
 * 2、如果事件参数类型是“非空类型”，那么只有[flowNotNull]存在，并且只有它发射的消息事件才能接收到。
 */
@OptIn(DelicateCoroutinesApi::class)
class Event(
    val hostClass: String,// 宿主类
    val tag: String,// 标签
    val requestCode: String,// 请求码。当标签相同时，可以使用请求码区分
    val paramType: String,// 被@BusObserver注解标注的方法的参数类型。只支持一个参数
    val isNullable: Boolean,
    val isSticky: Boolean,
    val callback: (Any, Any?) -> Unit
) {
    private var host: Any? = null// 宿主
    private var owner: LifecycleOwner? = null// 宿主所属的生命周期类
    private var job: Job? = null
    private val flowNullable: MutableSharedFlow<Any?>? by lazy {
        if (isNullable) {
            FlowManager.findFlowOrCreateIfAbsent(tag, requestCode, paramType, true, isSticky)
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
        FlowManager.findFlowOrCreateIfAbsent(tag, requestCode, notNullParamType, false, isSticky)
    }

    fun getHost() = host
    fun getOwner() = owner

    /**
     * 绑定事件到宿主和生命周期类
     */
    fun bind(host: Any, owner: LifecycleOwner?) {
        this.host = host
        this.owner = owner

        val scope = owner?.lifecycleScope ?: GlobalScope
        job = scope.launch(Dispatchers.Main) {
            launch {
                flowNullable?.collect {
                    callback(host, it)
                }
            }
            launch {
                flowNotNull?.filterNotNull()?.collect {
                    callback(host, it)
                }
            }
        }.apply {
            invokeOnCompletion {
                Log.w(TAG, "解绑事件 --> ${this@Event}")
                this@Event.host = null
                this@Event.owner = null
                this@Event.job = null
                EventManager.logHostAndOwner()
            }
        }
    }

    /**
     * 解绑事件的宿主和生命周期类
     */
    fun unbind() {
        this.job?.cancel()
    }

    fun post(data: Any?, isNullable: Boolean) {
        val scope = owner?.lifecycleScope ?: GlobalScope
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
        return "Event(${if (host != null) "host=$host" else "hostClass=$hostClass isNullable=${flowNullable != null}"}, \n flowNullable=$flowNullable \n flowNotNull=$flowNotNull)"
    }

}
