package com.like.floweventbus

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableSharedFlow

object EventManager {
    private val mGson = Gson()
    private val mEventList = mutableListOf<Event<*>>()

    init {
        try {
            val methodsClass = Class.forName("com.like.floweventbus_compiler.FlowEventbusMethods")
            methodsClass.declaredFields.forEach {
                val methods = mGson.fromJson<List<MethodInfo>>(
                    it.get(null)?.toString(),
                    object : TypeToken<List<MethodInfo>>() {}.type
                )
                Log.i(TAG, "获取到方法 --> $methods")
                // 订阅事件
                for (methodInfo in methods) {
                    for (tag in methodInfo.tags) {
                        subscribeEvent(
                            methodInfo.hostClass,
                            tag,
                            methodInfo.requestCode,
                            methodInfo.isSticky,
                            Class.forName(methodInfo.paramType)
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "获取方法失败 --> ${e.message}")
        }
    }

    private fun <T> subscribeEvent(
        hostClass: String,
        tag: String,
        requestCode: String,
        isSticky: Boolean,
        paramType: Class<T>
    ) {
        val flow = (mEventList.firstOrNull {
            // Flow由tag、requestCode组合决定
            it.tag == tag && it.requestCode == requestCode
        }?.flow as? MutableSharedFlow<T>) ?: MutableSharedFlow(
            replay = if (isSticky) 1 else 0,
            extraBufferCapacity = Int.MAX_VALUE //避免挂起导致数据发送失败
        )
        with(Event(hostClass, tag, requestCode, isSticky, flow)) {
            mEventList.add(this)
            Log.i(TAG, "订阅事件 --> $this")
            onCancel = {
                Log.w(TAG, "取消事件 --> $this")
                mEventList.remove(this)// event由host、tag、requestCode组合决定
                logEvent()
            }
        }
    }

    @JvmStatic
    fun registerHost(host: Any, owner: LifecycleOwner?) {
        val isRegistered = mEventList.any { it.host == host }
        if (isRegistered) {
            Log.e(TAG, "注册宿主失败 --> $host 已经注册过")
            return
        }

        val events = mEventList.filter {
            it.hostClass == host.javaClass.name
        }
        if (events.isEmpty()) {
            Log.e(TAG, "注册宿主失败 --> $host 不是宿主类，无需注册！")
            return
        }
        events.forEach {
            it.bind(host, owner) {
            }
        }
        Log.e(TAG, "注册宿主 --> $host")
    }

    fun <T> post(tag: String, requestCode: String, data: T) {
        val events = mEventList.filter {
            it.tag == tag && it.requestCode == requestCode
        }
        if (events.isEmpty()) {
            Log.e(TAG, "发送消息失败，没有订阅事件 --> tag=$tag${if (requestCode.isNotEmpty()) ", requestCode='$requestCode'" else ""}")
            return
        }
        val event = events.first() as Event<T>
        Log.v(TAG, "发送消息 --> $event，内容=$data")
        event.post(data)
    }

    fun removeHost(host: Any) {
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
