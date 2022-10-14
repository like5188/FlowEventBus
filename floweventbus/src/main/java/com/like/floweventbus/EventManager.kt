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

    fun getEvent(tag: String, requestCode: String, paramType: String, isNullable: Boolean): Event? =
        // 同一个 MutableSharedFlow，取任意一个即可
        mEventList.firstOrNull {
            it.flow.tag == tag && it.flow.requestCode == requestCode && isParamCompat(paramType, isNullable, it)
        }

    /**
     * 由自动生成的代码来调用
     */
    @JvmStatic
    fun addEvent(
        hostClass: String,
        tag: String,
        requestCode: String,
        paramType: String,
        isNullable: Boolean,
        isSticky: Boolean,
        callback: (Any, Any?) -> Unit
    ) {
        // 同一个 MutableSharedFlow，取任意一个即可
        val flow = getEvent(tag, requestCode, paramType, isNullable)?.flow ?: FlowWrapper(
            tag, requestCode, paramType, isNullable, isSticky, MutableSharedFlow(
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
