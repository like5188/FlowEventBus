package com.like.livedatabus

import androidx.lifecycle.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class Event<T>(
    val host: Any,// 宿主
    val owner: LifecycleOwner?,// 宿主所属的生命周期类
    val tag: String,// 标签
    val requestCode: String,// 请求码。当标签相同时，可以使用请求码区分
    val observer: Observer<T>,// 数据改变监听器
    val flow: MutableSharedFlow<T>
) {
    private var job: Job? = null

    init {
        if (owner == null) {
            job = GlobalScope.launch {
                flow.collect {
                    observer.onChanged(it)
                }
            }
        } else {
            job = owner.lifecycleScope.launch {
                owner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    flow.collect {
                        observer.onChanged(it)
                    }
                }
            }
        }
    }

    fun cancel() {
        job?.cancel()
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

    override fun toString(): String {
        return "Event(host=$host, tag='$tag'${if (requestCode.isNotEmpty()) ", requestCode='$requestCode'" else ""})"
    }

}
