package com.like.floweventbus

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.like.floweventbus_compiler.MethodInfo
import kotlinx.coroutines.flow.MutableSharedFlow

object EventManager {
    private val mGson = Gson()
    val mEventList = mutableListOf<Event<*>>()

    init {
        try {
            val methodsClass = Class.forName("com.like.floweventbus_compiler.FlowEventbusMethods")
            for (field in methodsClass.declaredFields) {
                if (field.name == "INSTANCE") {// 除去 kotlin 类中的隐藏属性
                    continue
                }
                val methods = mGson.fromJson<List<MethodInfo>>(
                    field.get(null).toString(),
                    object : TypeToken<List<MethodInfo>>() {}.type
                )
                // 订阅事件
                for (methodInfo in methods) {
                    for (tag in methodInfo.tags) {
                        addEvent(
                            methodInfo.hostClass.javaPrimitiveTypeToKotlin(),
                            tag,
                            methodInfo.requestCode,
                            methodInfo.isSticky,
                            methodInfo.methodName,
                            methodInfo.paramType.javaPrimitiveTypeToKotlin()
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
        val flow = getCachedFlowOrCreateIfAbsent(tag, requestCode, paramType)
        with(Event(hostClass, tag, requestCode, isSticky, flow, methodName, paramType)) {
            mEventList.add(this)
            Log.i(TAG, "添加事件 --> $this")
            logEvent()
        }
    }

    private fun <T> getCachedFlowOrCreateIfAbsent(tag: String, requestCode: String, paramType: Class<T>): MutableSharedFlow<T> {
        val isSticky = mEventList.any { it.tag == tag && it.requestCode == requestCode && it.isSticky }
        val cachedFlow = mEventList.firstOrNull {
            // Flow 由 tag、requestCode、paramType 组合决定
            it.tag == tag && it.requestCode == requestCode && it.paramType == paramType
        }?.flow as? MutableSharedFlow<T>
        return cachedFlow ?: MutableSharedFlow(
            replay = if (isSticky) 1 else 0,
            extraBufferCapacity = Int.MAX_VALUE // 避免挂起导致数据发送失败
        )
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

    inline fun <reified T> post(tag: String, requestCode: String, data: T) {
        // tag、requestCode、paramType 对应的所有事件，它们用了同一个 MutableSharedFlow
        val events = mEventList.filter {
            // 因为使用 kotlin 代码发送数据时，T::class.java 会自动装箱，所以需要装箱后再比较。
            it.tag == tag && it.requestCode == requestCode && it.paramType.box() == T::class.java
        }
        if (events.isEmpty()) {
            Log.e(
                TAG,
                "发送消息失败，没有订阅事件，或者参数类型不匹配 --> tag=$tag${if (requestCode.isNotEmpty()) ", requestCode='$requestCode'" else ""}, 数据=$data (${T::class.java.name})"
            )
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
