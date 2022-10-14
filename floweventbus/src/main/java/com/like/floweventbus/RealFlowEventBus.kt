package com.like.floweventbus

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.reflect.typeOf

object RealFlowEventBus {
    private val initialized = AtomicBoolean(false)

    /**
     * 此方法会[Initializer]类的所有实现类（floweventbus_compiler中自动生成的FlowEventbusInitializer类）的 init() 方法，然后触发 [EventManager.addEvent] 方法去添加所有被注解方法对应的[Event]。
     * FlowEventbusInitializer类在每个模块对应一个，和模块的BuildConfig类的包名一致。
     */
    fun init() {
        if (initialized.compareAndSet(false, true)) {
            Log.d(TAG, "开始初始化")
            try {
                ServiceLoader.load(Initializer::class.java).toList().forEach {
                    it.init()
                }
                Log.d(TAG, "初始化成功")
            } catch (e: Exception) {
                Log.e(TAG, "初始化失败 ${e.message}")
            }
        } else {
            Log.d(TAG, "已经初始化过了")
        }
    }

    fun register(host: Any, owner: LifecycleOwner?) {
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
        EventManager.logHostAndOwner()
    }

    inline fun <reified T> post(tag: String, requestCode: String, data: T) {
        // tag、requestCode、paramType 对应的所有事件，它们用了同一个 MutableSharedFlow
        val isNullable = typeOf<T>().isMarkedNullable
        // 为了和 com.like.floweventbus_compiler.Generator 中存储的参数类型一样。需要做下面的转换
        val canonicalName = T::class.java.canonicalName
        val paramType = when {
            canonicalName == "java.lang.Byte" && !isNullable -> "byte"
            canonicalName == "java.lang.Short" && !isNullable -> "short"
            canonicalName == "java.lang.Integer" && !isNullable -> "int"
            canonicalName == "java.lang.Long" && !isNullable -> "long"
            canonicalName == "java.lang.Float" && !isNullable -> "float"
            canonicalName == "java.lang.Double" && !isNullable -> "double"
            canonicalName == "java.lang.Character" && !isNullable -> "char"
            canonicalName == "java.lang.Boolean" && !isNullable -> "boolean"
            else -> canonicalName
        }
        val event = EventManager.getEvent(tag, requestCode, paramType, isNullable)
        val logMessage =
            "tag=$tag${if (requestCode.isNotEmpty()) ", requestCode='$requestCode'" else ""}, 数据=$data ($paramType ${if (isNullable) "nullable" else "notNull"})"
        if (event == null) {
            Log.e(TAG, "发送消息失败，没有订阅事件，或者参数类型不匹配 --> $logMessage")
            return
        }
        Log.v(TAG, "发送消息 --> $logMessage")
        // 同一个 MutableSharedFlow，取任意一个即可
        event.post(data)
    }

    fun unregister(host: Any) {
        EventManager.getEventList(host).listIterator().forEach {
            it.unbind()
        }
    }

}
