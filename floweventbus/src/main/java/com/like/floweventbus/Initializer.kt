package com.like.floweventbus

import android.util.Log
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 初始化工具
 */
class Initializer {
    companion object {
        private const val INIT_METHOD = "init"
    }

    private var initialized: AtomicBoolean = AtomicBoolean()

    /**
     * 处理注解，注册的时候调用。
     * 此方法会调用 FlowEventbusInitializer.init() 方法，然后触发 [EventManager.addEvent] 方法去添加所有被注解方法对应的[Event]。
     */
    fun initialize(host: Any) {
        if (initialized.compareAndSet(false, true)) {
            val hostPackageName = host.javaClass.`package`?.name
            if (hostPackageName.isNullOrEmpty()) {
                Log.e(TAG, "初始化失败 --> 获取宿主 $host 的包名失败")
                return
            }
            Log.d("Logger", "hostPackageName=$hostPackageName")
            val s = hostPackageName.split(".")
            val sb = StringBuilder()
            (s.indices).forEach {
                sb.append("${s[it]}.")
                val buildConfigClassName = "${sb}BuildConfig"
                Log.d("Logger", "buildConfigClassName=$buildConfigClassName")
                try {
                    Class.forName(buildConfigClassName)// 找到了 BuildConfig 类
                    val flowEventbusInitializerClassName = "${sb}FlowEventbusInitializer"
                    Log.d("Logger", "flowEventbusInitializerClassName=$flowEventbusInitializerClassName")
                    val clazz = Class.forName(flowEventbusInitializerClassName)
                    clazz.getDeclaredMethod(INIT_METHOD).invoke(clazz.kotlin.objectInstance)
                    Log.e(TAG, "初始化成功")
                    return
                } catch (e: Exception) {
                }
            }
            Log.e(TAG, "初始化失败")
        } else {
            Log.d(TAG, "已经初始化过了")
        }
    }
}