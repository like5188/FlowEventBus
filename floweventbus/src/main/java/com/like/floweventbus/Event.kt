package com.like.floweventbus

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.*

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

        // 由于 repeatOnLifecycle 是一个挂起函数，
        // 因此从 lifecycleScope 中创建新的协程
        job = (owner?.lifecycleScope?.launch(Dispatchers.Main) {
            // 直到 lifecycle 进入 DESTROYED 状态前都将当前协程挂起。
            // repeatOnLifecycle 每当生命周期处于 STARTED 或以后的状态时会在新的协程中
            // 启动执行代码块，并在生命周期进入 STOPPED 时取消协程。
            owner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collect {
                    callback(host, it)
                }
            }
        } ?: GlobalScope.launch(Dispatchers.Main) {
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
        scope.launch(Dispatchers.Main) {
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
