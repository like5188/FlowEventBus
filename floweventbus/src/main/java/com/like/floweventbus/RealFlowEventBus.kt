package com.like.floweventbus

import android.annotation.SuppressLint
import android.content.Context
import android.os.Process
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.like.floweventbus.FlowEventBus.register
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.reflect.typeOf

@SuppressLint("StaticFieldLeak")
object RealFlowEventBus {
    private val initialized = AtomicBoolean(false)
    lateinit var context: Context

    /**
     * 初始化，添加所有事件。
     * 此方法会调用[Initializer]类的所有实现类（floweventbus_compiler中自动生成的FlowEventbusInitializer类）的 init() 方法，然后触发 [EventManager.addEvent] 方法去添加所有被注解方法对应的[Event]。
     * FlowEventbusInitializer类在每个组件中对应一个，和组件的BuildConfig类的包名一致。
     * 注意：
     * 1、必须在[register]方法之前调用。
     * 2、跨进程时需要重新调用，所以推荐在application中调用。这样就能自动在跨进程时多次调用初始化了。
     */
    fun init(context: Context) {
        if (initialized.compareAndSet(false, true)) {
            Log.d(TAG, "开始初始化 [pid:${Process.myPid()}]")
            RealFlowEventBus.context = context.applicationContext
            try {
                // 初始化 Initializer 的实现类，即添加所有事件
                ServiceLoader.load(Initializer::class.java).toList().forEach {
                    it.init()
                }
                // 注册广播接收器
                IpcReceiver.register(RealFlowEventBus.context)
                Log.d(TAG, "初始化成功")
            } catch (e: Exception) {
                Log.e(TAG, "初始化失败 ${e.message}")
            }
        } else {
            Log.d(TAG, "已经初始化过了")
        }
    }

    fun bind(host: Any, owner: LifecycleOwner?) {
        if (EventManager.isRegistered(host)) {
            Log.e(TAG, "绑定宿主失败 --> 宿主 $host 已经绑定过")
            return
        }

        val event = EventManager.getEvent(host.javaClass.name)
        if (event == null) {
            Log.e(TAG, "绑定宿主失败 --> $host 不是宿主类，不能绑定！")
            return
        }
        event.bind(host, owner)
    }

    inline fun <reified T> post(tag: String, requestCode: String, data: T) {
        // tag、requestCode、paramType 对应的所有事件，它们用了同一个 MutableSharedFlow
        val isNullable = typeOf<T>().isMarkedNullable
        val paramType = toJavaDataType<T>()
        val event = EventManager.getEvent(tag, requestCode, paramType, isNullable)
        val logMessage =
            "tag=$tag${if (requestCode.isNotEmpty()) ", requestCode='$requestCode'" else ""}${if (paramType == NoArgs::class.java.name) "" else ", 数据=$data [$paramType, ${if (isNullable) "nullable" else "notNull"}]"}"
        if (event == null) {
            Log.e(TAG, "[pid:${Process.myPid()}] 发送消息失败，没有订阅事件，或者参数类型不匹配 --> $logMessage")
            return
        }
        Log.v(TAG, "[pid:${Process.myPid()}] 发送消息 --> $logMessage")
        // 同一个 MutableSharedFlow，取任意一个即可
        event.post(data, isNullable)
    }

    inline fun <reified T> sendBroadcast(tag: String, requestCode: String, data: T) {
        IpcReceiver.sendBroadcast(context) {
            post(tag, requestCode, data)
        }
    }

    fun unbind(host: Any) {
        EventManager.getEvent(host)?.unbind(host)
    }

}
