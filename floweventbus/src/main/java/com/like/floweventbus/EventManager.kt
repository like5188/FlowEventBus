package com.like.floweventbus

import android.util.Log
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * 事件管理
 */
object EventManager {
    private val mEventList = mutableListOf<Event>()

    fun isRegistered(host: Any): Boolean = mEventList.any { it.getHost() == host }

    fun getEventList(hostClass: String): List<Event> = mEventList.filter { it.hostClass == hostClass }

    fun getEventList(host: Any): List<Event> = mEventList.filter { it.getHost() == host }

    fun getEvent(tag: String, requestCode: String, paramType: String): Event? =
        // 同一个 MutableSharedFlow，取任意一个即可
        mEventList.firstOrNull {
            // 因为使用 kotlin 代码发送数据时，T::class.java 会自动装箱，所以需要装箱后再比较，但是这里在自动生成的代码中已经做了装箱处理再传递过来的。
            it.flow.tag == tag && it.flow.requestCode == requestCode && it.flow.paramType == paramType
        }

    fun getEvent(hostClass: String, tag: String, requestCode: String, paramType: String): Event? =
        mEventList.firstOrNull {
            it.hostClass == hostClass && it.flow.tag == tag && it.flow.requestCode == requestCode && it.flow.paramType == paramType
        }

    /**
     * 由自动生成的代码来调用
     */
    fun addEvent(
        hostClass: String,
        tag: String,
        requestCode: String,
        paramType: String,
        isSticky: Boolean,
        callback: (Any, Any?) -> Unit
    ) {
        val oldEvent = getEvent(hostClass, tag, requestCode, paramType)
        if (oldEvent != null) {
            Log.e(TAG, "添加事件失败 --> 事件 $oldEvent 已经添加过")
            return
        }
        // 同一个 MutableSharedFlow，取任意一个即可
        val flow = getEvent(tag, requestCode, paramType)?.flow ?: FlowWrapper(
            tag, requestCode, paramType, isSticky, MutableSharedFlow(
                replay = if (isSticky) 1 else 0,
                extraBufferCapacity = if (isSticky) Int.MAX_VALUE else 0 // 避免挂起导致数据发送失败
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
        Log.v(TAG, "事件总数：${mEventList.size}")
        mEventList.forEach {
            Log.v(TAG, " --> $it")
        }
    }

    /**
     * 打印缓存的宿主和生命周期类
     */
    fun logHostAndOwner() {
        val hosts = mEventList.mapNotNull { it.getHost() }.distinct()
        Log.d(TAG, "宿主总数：${hosts.size}")
        hosts.forEach {
            Log.d(TAG, " --> $it")
        }

        val owners = mEventList.mapNotNull { it.getOwner() }.distinct()
        Log.d(TAG, "生命周期类总数：${owners.size}")
        owners.forEach {
            Log.d(TAG, " --> $it")
        }
    }

}
