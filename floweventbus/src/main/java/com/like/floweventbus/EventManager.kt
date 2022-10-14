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
     * 参数是否匹配
     */
    private fun isParamCompat(paramType: String, isNullable: Boolean, event: Event): Boolean {
        // 注意：可空类型可以接受不可空的值。
        return if (isNullable) {
            event.flow.paramType == paramType && event.flow.isNullable == isNullable
        } else {
            when (paramType) {
                "byte" -> event.flow.paramType == "byte" || event.flow.paramType == "java.lang.Byte"
                "short" -> event.flow.paramType == "short" || event.flow.paramType == "java.lang.Short"
                "int" -> event.flow.paramType == "int" || event.flow.paramType == "java.lang.Integer"
                "long" -> event.flow.paramType == "long" || event.flow.paramType == "java.lang.Long"
                "float" -> event.flow.paramType == "float" || event.flow.paramType == "java.lang.Float"
                "double" -> event.flow.paramType == "double" || event.flow.paramType == "java.lang.Double"
                "char" -> event.flow.paramType == "char" || event.flow.paramType == "java.lang.Character"
                "boolean" -> event.flow.paramType == "boolean" || event.flow.paramType == "java.lang.Boolean"
                else -> event.flow.paramType == paramType
            }
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
