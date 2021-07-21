package com.like.floweventbus

import androidx.lifecycle.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
class Event<T>(
    val hostClass: Class<*>,// 宿主类
    val tag: String,// 标签
    val requestCode: String,// 请求码。当标签相同时，可以使用请求码区分
    val isSticky: Boolean,
    val flow: MutableSharedFlow<T>
) {
    var host: Any? = null// 宿主
    var owner: LifecycleOwner? = null// 宿主所属的生命周期类
    private var observer: Observer<T>? = null// 数据改变回调
    private var job: Job? = null
    var onCancel: (() -> Unit)? = null

    fun bind(host: Any, owner: LifecycleOwner?, observer: Observer<T>) {
        this.host = host
        this.owner = owner
        this.observer = observer
        job = (owner?.lifecycleScope?.launch {
            owner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collect {
                    observer.onChanged(it)
                }
            }
        } ?: GlobalScope.launch {
            flow.collect {
                observer.onChanged(it)
            }
        }).apply {
            invokeOnCompletion {
                onCancel?.invoke()
            }
        }
    }

    fun post(data: T) {
        val scope = owner?.lifecycleScope ?: GlobalScope
        scope.launch {
            flow.emit(data)
        }
    }

    fun cancel() {
        job?.cancel()
    }

    override fun toString(): String {
        return "Event(host=$host, tag='$tag'${if (requestCode.isNotEmpty()) ", requestCode='$requestCode'" else ""}, isSticky='$isSticky')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Event<*>

        if (host != other.host) return false
        if (tag != other.tag) return false
        if (requestCode != other.requestCode) return false

        return true
    }

    override fun hashCode(): Int {
        var result = host.hashCode()
        result = 31 * result + tag.hashCode()
        result = 31 * result + requestCode.hashCode()
        return result
    }

}
