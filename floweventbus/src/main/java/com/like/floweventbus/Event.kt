package com.like.floweventbus

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*

/**
 * 1、如果事件参数类型是可空类型，那么[flowNullable]、[flowNotNull]都存在，这两个流发射的消息事件都能接收到。
 * 2、如果事件参数类型是非空类型，那么[flowNotNull]存在，只有它发射的消息事件才能接收到。
 */
@OptIn(DelicateCoroutinesApi::class)
class Event(
    val hostClass: String,// 宿主类
    val flowNullable: FlowWrapper<Any?>?,
    val flowNotNull: FlowWrapper<Any>?,
    val callback: (Any, Any?) -> Unit
) {
    private var host: Any? = null// 宿主
    private var owner: LifecycleOwner? = null// 宿主所属的生命周期类
    private var job: Job? = null

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
                flowNotNull?.collect {
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
