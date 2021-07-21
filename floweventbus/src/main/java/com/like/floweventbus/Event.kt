package com.like.floweventbus

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
class Event<T>(
    val owner: LifecycleOwner?,// 生命周期类
    val tag: String,// 标签
    val requestCode: String,// 请求码。当标签相同时，可以使用请求码区分
    private val isSticky: Boolean,
    private val callback: (T) -> Unit,// 数据改变回调
) {
    private val flow: MutableSharedFlow<T> = MutableSharedFlow(
        replay = if (isSticky) 1 else 0,
        extraBufferCapacity = Int.MAX_VALUE //避免挂起导致数据发送失败
    )
    private val job: Job = owner?.lifecycleScope?.launch {
        owner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect {
                callback(it)
            }
        }
    } ?: GlobalScope.launch {
        flow.collect {
            callback(it)
        }
    }

    fun post(data: T) {
        val scope = owner?.lifecycleScope ?: GlobalScope
        scope.launch {
            flow.emit(data)
        }
    }

    fun cancel() {
        job.cancel()
    }

    override fun toString(): String {
        return "Event(tag='$tag'${if (requestCode.isNotEmpty()) ", requestCode='$requestCode'" else ""}, isSticky='$isSticky')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Event<*>) return false

        if (tag != other.tag) return false
        if (requestCode != other.requestCode) return false

        return true
    }

    override fun hashCode(): Int {
        var result = tag.hashCode()
        result = 31 * result + requestCode.hashCode()
        return result
    }

}
