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
                Ipc.register(RealFlowEventBus.context)
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

        val hostClassEvents = EventManager.getEventList(host.javaClass.name)
        if (hostClassEvents.isEmpty()) {
            Log.e(TAG, "绑定宿主失败 --> $host 不是宿主类，不能绑定！")
            return
        }

        hostClassEvents.forEach {
            it.bind(host, owner)
        }
    }

    inline fun <reified T> sendBroadcast(tag: String, requestCode: String, data: T) {
        val isNullable = typeOf<T>().isMarkedNullable
        val paramType = T::class.java.canonicalName ?: ""
        Ipc.sendBroadcast(context, tag, requestCode, data, isNullable, paramType)
    }

    inline fun <reified T> post(tag: String, requestCode: String, data: T) {
        val isNullable = typeOf<T>().isMarkedNullable
        val paramType = T::class.java.canonicalName ?: ""
        doPost(tag, requestCode, data, isNullable, paramType)
    }

    fun doPost(tag: String?, requestCode: String?, data: Any?, isNullable: Boolean, paramType: String?) {
        // 获取可以发送数据的事件。
        val validEventList = EventManager.getEventList(tag, requestCode, paramType, isNullable)
        val logMessage =
            "tag=$tag${if (!requestCode.isNullOrEmpty()) ", requestCode='$requestCode'" else ""}${if (paramType == NoArgs::class.java.name) "" else ", 数据=$data [$paramType, ${if (isNullable) "nullable" else "notNull"}]"}"
        if (validEventList.isEmpty()) {
            Log.e(TAG, "[pid:${Process.myPid()}] 发送消息失败 --> $logMessage")
            return
        }
        Log.v(TAG, "[pid:${Process.myPid()}] 发送消息 --> $logMessage")
        validEventList.forEach {
            it.post(data)
        }
    }

    fun unbind(host: Any) {
        EventManager.getEventList(host).forEach {
            it.unbind(host)
        }
    }

}
