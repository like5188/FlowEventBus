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
     * @param paramType     发送的数据类型
     * @param isNullable    发送的数据类型是否为可空类型
     */
    fun getEventList(tag: String?, requestCode: String?, paramType: String?, isNullable: Boolean): List<Event> =
        mEventList.filter {
            it.tag == tag && it.requestCode == requestCode && if (isNullable) {
                it.paramType == paramType && it.isNullable// 如果发送的数据类型为可空类型，那么就只有可空类型的事件能发送它
            } else {
                it.paramType == paramType// 如果发送的数据类型为非空类型，那么可空、非空类型的事件都能发送它
            }
        }.distinct()

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
    /*
    paramType 是 java 数据类型，对应的 kotlin 数据类型需要转换，例如：
        java                    kotlin
        int                     Int
        java.lang.Integer       Int?
        int[]                   IntArray、IntArray?
        java.lang.Integer[]     Array<Int>、Array<Int>?、Array<Int?>、Array<Int?>?
    */
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
