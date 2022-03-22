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
            val s = hostPackageName.split(".")
            val sb = StringBuilder()
            (s.indices).forEach {
                sb.append("${s[it]}.")
                val flowEventbusInitializerClassName = "${sb}FlowEventbusInitializer"
                try {
                    val clazz = Class.forName(flowEventbusInitializerClassName)
                    clazz.getDeclaredMethod(INIT_METHOD).invoke(clazz.kotlin.objectInstance)
                    Log.d(TAG, "初始化成功 $flowEventbusInitializerClassName")
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