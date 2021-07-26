package com.like.floweventbus

import android.util.Log
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * 事件管理
 */
object EventManager {
    private val mEventList = mutableListOf<Event>()

    fun isRegistered(host: Any) = mEventList.any { it.getHost() == host }

    fun getEventList(hostClass: String) = mEventList.filter { it.hostClass == hostClass }

    fun getEventList(host: Any) = mEventList.filter { it.getHost() == host }

    fun getEventList(tag: String, requestCode: String, paramType: String) = mEventList.filter {
        // 因为使用 kotlin 代码发送数据时，T::class.java 会自动装箱，所以需要装箱后再比较，但是这里在自动生成的代码中已经做了装箱处理再传递过来的。
        it.flow.tag == tag && it.flow.requestCode == requestCode && it.flow.paramType == paramType
    }

    /**
     * 由自动生成的代码来调用
     */
    fun addEvent(
        hostClass: String,
        tag: String,
        requestCode: String,
        paramType: String,
        isStickyMethod: Boolean,
        callback: (Any, Any?) -> Unit
    ) {
        val flow = mEventList.firstOrNull {
            it.flow.tag == tag && it.flow.requestCode == requestCode && it.flow.paramType == paramType
        }?.flow ?: FlowWrapper(
            tag, requestCode, paramType, MutableSharedFlow(
                replay = if (isStickyMethod) 1 else 0,
                extraBufferCapacity = Int.MAX_VALUE // 避免挂起导致数据发送失败
            )
        )
        with(Event(hostClass, flow, callback)) {
            mEventList.add(this)
            Log.i(TAG, "添加事件 --> $this")
            logEvent()
        }
    }

    /**
     * 打印缓存的事件
     */
    fun logEvent() {
        val eventList = EventManager.mEventList
        Log.d(TAG, "事件总数：${eventList.size}${if (eventList.isEmpty()) "" else "，包含：$eventList"}")
    }

    /**
     * 打印缓存的宿主和生命周期类
     */
    fun logHostAndOwner() {
        val eventList = EventManager.mEventList
        val hosts = eventList.mapNotNull { it.getHost() }.distinct()
        Log.d(TAG, "宿主总数：${hosts.size}${if (hosts.isEmpty()) "" else "，包含：$hosts"}")

        val owners = eventList.mapNotNull { it.getHost() }.distinct()
        Log.d(TAG, "生命周期类总数：${owners.size}${if (owners.isEmpty()) "" else "，包含：$owners"}")
    }

}
