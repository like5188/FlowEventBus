package com.like.floweventbus

import android.util.Log
import androidx.lifecycle.LifecycleOwner

object EventManager {
    private val mEventList = mutableListOf<Event<*>>()

    fun <T> register(owner: LifecycleOwner?, tag: String, requestCode: String, isSticky: Boolean, callback: (T) -> Unit) {
        if (tag.isEmpty()) {
            Log.e(TAG, "订阅过事件失败，tag 不能为空")
            return
        }
        getCachedEvent<T>(tag, requestCode) ?: mEventList.add(Event(owner, tag, requestCode, isSticky, callback))
        logEvent()
    }

    fun <T> post(tag: String, requestCode: String, data: T) {
        val requestCodeLogMessage = if (requestCode.isNotEmpty()) ", requestCode='$requestCode'" else ""
        val event = getCachedEvent<T>(tag, requestCode)
        if (event == null) {
            Log.e(TAG, "发送消息失败，没有订阅事件： --> tag=$tag$requestCodeLogMessage")
            return
        }
        Log.v(TAG, "发送消息 --> tag=$tag$requestCodeLogMessage，内容=$data")
        event.post(data)
    }

    /**
     * 获取缓存的[Event]对象。
     */
    private fun <T> getCachedEvent(tag: String, requestCode: String): Event<T>? {
        return mEventList.firstOrNull {
            it.tag == tag && it.requestCode == requestCode
        } as Event<T>?
    }

    /**
     * 打印缓存的事件
     */
    private fun logEvent() {
        val events = mEventList.toSet()
        Log.d(TAG, "事件总数：${events.size}${if (events.isEmpty()) "" else "，包含：$events"}")

        val owners = mEventList.distinctBy { it.owner }.map { if (it.owner != null) it.owner::class.java.name else "null" }
        Log.d(TAG, "生命周期类总数：${owners.size}${if (owners.isEmpty()) "" else "，包含：$owners"}")
    }

}
