package com.like.floweventbus

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import kotlinx.coroutines.flow.MutableSharedFlow

object EventManager {
    private val mEventList = mutableListOf<Event<*>>()

    fun isRegistered(host: Any) = mEventList.any { it.host == host }

    @JvmStatic
    fun <T> register(host: Any, owner: LifecycleOwner?, tag: String, requestCode: String, isSticky: Boolean, observer: Observer<T>) {
        if (tag.isEmpty()) {
            Log.e(TAG, "订阅事件失败 --> tag 不能为空")
            return
        }

        val flow = (mEventList.firstOrNull {
            // Flow由tag、requestCode组合决定
            it.tag == tag && it.requestCode == requestCode
        }?.flow as MutableSharedFlow<T>?) ?: MutableSharedFlow(
            replay = if (isSticky) 1 else 0,
            extraBufferCapacity = Int.MAX_VALUE //避免挂起导致数据发送失败
        )
        with(Event(host, owner, tag, requestCode, isSticky, observer, flow)) {
            if (mEventList.contains(this)) {// event由host、tag、requestCode组合决定
                Log.e(TAG, "已经订阅过事件 --> $this")
                return
            }
            mEventList.add(this)
            Log.i(TAG, "订阅事件 --> $this")
            onCancel = {
                Log.w(TAG, "取消事件 --> $this")
                mEventList.remove(this)
                logEvent()
            }
        }
        logEvent()
    }

    fun <T> post(tag: String, requestCode: String, data: T) {
        val events = mEventList.filter {
            it.tag == tag && it.requestCode == requestCode
        }
        if (events.isEmpty()) {
            Log.e(TAG, "发送消息失败，没有订阅事件 --> tag=$tag${if (requestCode.isNotEmpty()) ", requestCode='$requestCode'" else ""}")
            return
        }
        val event = events.first() as Event<T>
        Log.v(TAG, "发送消息 --> $event，内容=$data")
        event.post(data)
    }

    fun removeHost(host: Any) {
        mEventList.filter { it.host == host }.listIterator().forEach {
            it.cancel()
        }
    }

    /**
     * 打印缓存的事件
     */
    private fun logEvent() {
        Log.d(TAG, "事件总数：${mEventList.size}${if (mEventList.isEmpty()) "" else "，包含：$mEventList"}")

        val hosts = mEventList.distinctBy { it.host }.map { it.host }
        Log.d(TAG, "宿主总数：${hosts.size}${if (hosts.isEmpty()) "" else "，包含：$hosts"}")

        val owners = mEventList.distinctBy { it.owner }.map { if (it.owner != null) it.owner::class.java.name else "null" }
        Log.d(TAG, "生命周期类总数：${owners.size}${if (owners.isEmpty()) "" else "，包含：$owners"}")
    }

}
