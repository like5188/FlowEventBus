package com.like.floweventbus

import android.util.Log
import androidx.lifecycle.LifecycleOwner

object RealFlowEventBus {
    private var initialized: Boolean = false

    /**
     * 处理注解，注册的时候调用。
     * 此方法会调用 FlowEventbusMethods.initialize() 方法，然后触发 [EventManager.addEvent] 方法。
     */
    private fun handleAnnotations() {
        if (initialized) {
            return
        }
        initialized = true
        try {
            val clazz = Class.forName("com.like.floweventbus.FlowEventbusMethods")
            clazz.getDeclaredMethod("initialize").invoke(clazz.kotlin.objectInstance)
        } catch (e: Exception) {
            Log.e(TAG, "处理注解信息失败 --> ${e.message}")
        }
    }

    fun register(host: Any, owner: LifecycleOwner?) {
        handleAnnotations()
        if (EventManager.isRegistered(host)) {
            Log.e(TAG, "绑定宿主失败 --> 宿主 $host 已经绑定过")
            return
        }

        // 宿主对应的所有事件
        val hostEvents = EventManager.getEventList(host.javaClass.name)
        if (hostEvents.isEmpty()) {
            Log.e(TAG, "绑定宿主失败 --> $host 不是宿主类，不能绑定！")
            return
        }
        hostEvents.forEach { event ->
            event.bind(host, owner)
        }
        Log.i(TAG, "绑定宿主 --> $host")
        EventManager.logEvent()
        EventManager.logHostAndOwner()
    }

    inline fun <reified T> post(tag: String, requestCode: String, data: T) {
        // tag、requestCode、paramType 对应的所有事件，它们用了同一个 MutableSharedFlow
        val events = EventManager.getEventList(tag, requestCode, T::class.java.name)
        val logMessage = "tag=$tag${if (requestCode.isNotEmpty()) ", requestCode='$requestCode'" else ""}, 数据=$data (${T::class.java.name})"
        if (events.isEmpty()) {
            Log.e(TAG, "发送消息失败，没有订阅事件，或者参数类型不匹配 --> $logMessage")
            return
        }
        Log.v(TAG, "发送消息 --> $logMessage")
        // 同一个 MutableSharedFlow，取任意一个即可
        events.first().post(data)
    }

    fun unregister(host: Any) {
        EventManager.getEventList(host).listIterator().forEach {
            it.unbind()
        }
    }

}
