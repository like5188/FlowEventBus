package com.like.floweventbus

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
class Event(
    val hostClass: String,// 宿主类
    val flow: FlowWrapper<Any?>,
    val callback: (Any, Any?) -> Unit
) {
    private var host: Any? = null// 宿主
    private var owner: LifecycleOwner? = null// 宿主所属的生命周期类
    private var job: Job? = null

    fun getHost() = host

    fun bind(host: Any, owner: LifecycleOwner?) {
        this.host = host
        this.owner = owner

        job = (owner?.lifecycleScope?.launch {
            owner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collect {
                    callback(host, it)
                }
            }
        } ?: GlobalScope.launch {
            flow.collect {
                callback(host, it)
            }
        }).apply {
            invokeOnCompletion {
                Log.w(TAG, "解绑事件 --> ${this@Event}")
                this@Event.host = null
                this@Event.owner = null
                this@Event.job = null
                EventManager.logEvent()
                EventManager.logHostAndOwner()
            }
        }
    }

    fun unbind() {
        this.job?.cancel()
    }

    fun post(data: Any?) {
        val scope = owner?.lifecycleScope ?: GlobalScope
        scope.launch {
            flow.emit(data)
        }
    }

    override fun toString(): String {
        return "Event(host=$host, flow=$flow)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Event) return false

        if (flow != other.flow) return false
        if (host != other.host) return false

        return true
    }

    override fun hashCode(): Int {
        var result = flow.hashCode()
        result = 31 * result + (host?.hashCode() ?: 0)
        return result
    }

}
