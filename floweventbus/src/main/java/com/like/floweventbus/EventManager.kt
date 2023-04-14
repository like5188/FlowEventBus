package com.like.floweventbus

import android.util.Log

/**
 * 事件管理
 */
object EventManager {
    private val mEventList = mutableListOf<Event>()

    fun isRegistered(host: Any): Boolean = mEventList.any { it.hosts.contains(host) }

    fun getEventList(hostClass: String): List<Event> = mEventList.filter { it.hostClass == hostClass }

    fun getEventList(host: Any): List<Event> = mEventList.filter { it.hosts.contains(host) }

    /**
     * @param paramType     发送的数据的参数类型
     * @param isNullable    发送的数据的参数类型是否为可空类型
     */
    fun getEvent(tag: String?, requestCode: String?, paramType: String?, isNullable: Boolean): Event? =
        mEventList.firstOrNull {
            it.tag == tag && it.requestCode == requestCode && if (isNullable) {
                it.paramType == paramType && it.isNullable == isNullable
            } else {
                it.paramType == paramType// 可空类型和非空类型的参数类型都可以接收非空类型的数据
            }
        }

    /**
     * 由自动生成的代码来调用
     */
    @JvmStatic
    fun addEvent(
        hostClass: String,
        tag: String,
        requestCode: String,
        isSticky: Boolean,
        paramType: String,
        isNullable: Boolean,
        callback: (Any, Any?) -> Unit
    ) {
        val event = Event(hostClass, tag, requestCode, isSticky, paramType, isNullable, callback)
        mEventList.add(event)
        Log.i(TAG, "添加事件(${mEventList.size}) --> $event")
    }

    /**
     * 打印缓存的宿主
     */
    fun logHost() {
        val hosts = mEventList.flatMap { it.hosts }.distinct()
        if (hosts.isEmpty()) {
            Log.d(TAG, "宿主(0)")
            return
        }
        hosts.forEachIndexed { index, any ->
            Log.d(TAG, "宿主(${index + 1}) --> $any")
        }
    }

}
