package com.like.floweventbus

import android.util.Log
import androidx.lifecycle.LifecycleOwner

object EventManager {

    fun register(host: Any, owner: LifecycleOwner?) {
        Bridge.handleAnnotations()
        val eventList = Bridge.mEventList
        val isRegistered = eventList.any { it.getHost() == host }
        if (isRegistered) {
            Log.e(TAG, "绑定宿主失败 --> 宿主 $host 已经绑定过")
            return
        }

        // 宿主对应的所有事件
        val hostEvents = eventList.filter {
            it.hostClass == host.javaClass.name
        }
        if (hostEvents.isEmpty()) {
            Log.e(TAG, "绑定宿主失败 --> $host 不是宿主类，不能绑定！")
            return
        }
        hostEvents.forEach { event ->
            event.bind(host, owner)
        }
        Log.i(TAG, "绑定宿主 --> $host")
        logEvent()
        logHostAndOwner()
    }

    inline fun <reified T> post(tag: String, requestCode: String, data: T) {
        // tag、requestCode、paramType 对应的所有事件，它们用了同一个 MutableSharedFlow
        val events = Bridge.mEventList.filter {
            // 因为使用 kotlin 代码发送数据时，T::class.java 会自动装箱，所以需要装箱后再比较，但是这里在自动生成的代码中已经做了装箱处理再传递过来的。
            it.flow.tag == tag && it.flow.requestCode == requestCode && it.flow.paramType == T::class.java.name
        }
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
        Bridge.mEventList.filter { it.getHost() == host }.listIterator().forEach {
            it.unbind()
        }
    }

    /**
     * 打印缓存的事件
     */
    fun logEvent() {
        val eventList = Bridge.mEventList
        Log.d(TAG, "事件总数：${eventList.size}${if (eventList.isEmpty()) "" else "，包含：$eventList"}")
    }

    /**
     * 打印缓存的宿主和生命周期类
     */
    fun logHostAndOwner() {
        val eventList = Bridge.mEventList
        val hosts = eventList.mapNotNull { it.getHost() }.distinct()
        Log.d(TAG, "宿主总数：${hosts.size}${if (hosts.isEmpty()) "" else "，包含：$hosts"}")

        val owners = eventList.mapNotNull { it.getHost() }.distinct()
        Log.d(TAG, "生命周期类总数：${owners.size}${if (owners.isEmpty()) "" else "，包含：$owners"}")
    }
}
