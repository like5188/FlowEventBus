package com.like.floweventbus

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.like.floweventbus_compiler.MethodInfo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlin.reflect.full.declaredMemberProperties

object EventManager {
    private val mGson = Gson()
    private val mEventList = mutableListOf<Event<*>>()

    init {
        try {
            val methodsClass = Class.forName("com.like.floweventbus_compiler.FlowEventbusMethods").kotlin
            methodsClass.declaredMemberProperties.forEach {
                val methods = mGson.fromJson<List<MethodInfo>>(
                    it.call().toString(),
                    object : TypeToken<List<MethodInfo>>() {}.type
                )
                // 订阅事件
                for (methodInfo in methods) {
                    for (tag in methodInfo.tags) {
                        addEvent(
                            methodInfo.hostClass.toClass(),
                            tag,
                            methodInfo.requestCode,
                            methodInfo.isSticky,
                            methodInfo.methodName,
                            methodInfo.paramType.toClass()
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "获取注解方法信息失败 --> ${e.message}")
        }
    }

    private fun <T> addEvent(
        hostClass: Class<*>,
        tag: String,
        requestCode: String,
        isSticky: Boolean,
        methodName: String,
        paramType: Class<T>
    ) {
        val flow = (mEventList.firstOrNull {
            // Flow 由 tag、requestCode 组合决定
            it.tag == tag && it.requestCode == requestCode
        }?.flow as? MutableSharedFlow<T>) ?: MutableSharedFlow(
            replay = if (isSticky) 1 else 0,
            extraBufferCapacity = Int.MAX_VALUE // 避免挂起导致数据发送失败
        )
        with(Event(hostClass, tag, requestCode, isSticky, flow, methodName, paramType)) {
            mEventList.add(this)
            Log.i(TAG, "添加事件 --> $this")
            logEvent()
        }
    }

    fun register(host: Any, owner: LifecycleOwner?) {
        val isRegistered = mEventList.any { it.host == host }
        if (isRegistered) {
            Log.e(TAG, "绑定宿主失败 --> 宿主 $host 已经绑定过")
            return
        }

        // 宿主对应的所有事件
        val hostEvents = mEventList.filter {
            it.hostClass == host.javaClass
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

    fun <T> post(tag: String, requestCode: String, data: T) {
        // tag、requestCode 对应的所有事件，它们用了同一个 MutableSharedFlow
        val events = mEventList.filter {
            it.tag == tag && it.requestCode == requestCode
        }
        if (events.isEmpty()) {
            Log.e(TAG, "发送消息失败，没有订阅事件 --> tag=$tag${if (requestCode.isNotEmpty()) ", requestCode='$requestCode'" else ""}")
            return
        }
        // 同一个 MutableSharedFlow，取任意一个即可
        val event = events.first() as Event<T>
        Log.v(TAG, "发送消息 --> tag=$tag${if (requestCode.isNotEmpty()) ", requestCode='$requestCode'" else ""}，内容=$data")
        event.post(data)
    }

    fun unregister(host: Any) {
        mEventList.filter { it.host == host }.listIterator().forEach {
            it.unbind()
        }
    }

    /**
     * 打印缓存的事件
     */
    fun logEvent() {
        Log.d(TAG, "事件总数：${mEventList.size}${if (mEventList.isEmpty()) "" else "，包含：$mEventList"}")
    }

    /**
     * 打印缓存的宿主和生命周期类
     */
    fun logHostAndOwner() {
        val hosts = mEventList.mapNotNull { it.host }.distinct()
        Log.d(TAG, "宿主总数：${hosts.size}${if (hosts.isEmpty()) "" else "，包含：$hosts"}")

        val owners = mEventList.mapNotNull { it.owner }.distinct()
        Log.d(TAG, "生命周期类总数：${owners.size}${if (owners.isEmpty()) "" else "，包含：$owners"}")
    }
}
