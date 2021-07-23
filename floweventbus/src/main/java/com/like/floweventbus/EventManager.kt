package com.like.floweventbus

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlin.reflect.full.staticProperties

object EventManager {
    private val mGson = Gson()
    private val mEventList = mutableListOf<Event<*>>()

    init {
        try {
            val methodsClass = Class.forName("com.like.floweventbus_compiler.FlowEventbusMethods").kotlin
            methodsClass.staticProperties.forEach {
                val methods = mGson.fromJson<List<MethodInfo>>(
                    it.get().toString(),
                    object : TypeToken<List<MethodInfo>>() {}.type
                )
                Log.i(TAG, "获取到属性 --> $methods")
                // 订阅事件
                for (methodInfo in methods) {
                    for (tag in methodInfo.tags) {
                        addEvent(
                            Class.forName(methodInfo.hostClass),
                            tag,
                            methodInfo.requestCode,
                            methodInfo.isSticky,
                            methodInfo.methodName,
                            Class.forName(methodInfo.paramType)
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "获取方法失败 --> ${e.message}")
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
            Log.i(TAG, "订阅事件 --> $this")
            onCancel = {
                Log.w(TAG, "取消事件 --> $this")
                mEventList.remove(this)// event 由 host、tag、requestCode 组合决定
                logEvent()
            }
        }
    }

    fun register(host: Any, owner: LifecycleOwner?) {
        val isRegistered = mEventList.any { it.host == host }
        if (isRegistered) {
            Log.e(TAG, "注册宿主失败 --> $host 已经注册过")
            return
        }

        // 宿主对应的所有事件
        val hostEvents = mEventList.filter {
            it.hostClass == host.javaClass
        }
        if (hostEvents.isEmpty()) {
            Log.e(TAG, "注册宿主失败 --> $host 不是宿主类，无需注册！")
            return
        }
        hostEvents.forEach { event ->
            event.bind(host, owner)
        }
        Log.i(TAG, "注册宿主 --> $host")
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
        Log.v(TAG, "发送消息 --> $event，内容=$data")
        event.post(data)
    }

    fun unregister(host: Any) {
        mEventList.filter { it.host == host }.listIterator().forEach {
            it.cancel()
        }
    }

    /**
     * 打印缓存的事件
     */
    private fun logEvent() {
        Log.d(TAG, "事件总数：${mEventList.size}${if (mEventList.isEmpty()) "" else "，包含：$mEventList"}")

        val hosts = mEventList.distinctBy { it.host }.map { it.host }
        Log.d(TAG, "宿主总数：${hosts.size}${if (hosts.isEmpty()) "" else "，包含：$hosts"}")

        val owners = mEventList.distinctBy { it.owner }.map { if (it.owner != null) it.owner!!::class.java.name else "null" }
        Log.d(TAG, "生命周期类总数：${owners.size}${if (owners.isEmpty()) "" else "，包含：$owners"}")
    }

}
