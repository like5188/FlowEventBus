package com.like.floweventbus

import android.util.Log

/**
 * 初始化工具
 */
object Initializer {
    private const val INIT_METHOD = "init"
    private val initializedHost = mutableListOf<String>()

    /**
     * 此方法会调用 FlowEventbusInitializer.init() 方法，然后触发 [EventManager.addEvent] 方法去添加所有被注解方法对应的[Event]。
     * FlowEventbusInitializer类在每个模块对应一个，和模块的BuildConfig类的包名一致。
     */
    fun initialize(host: Any) {
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
            if (initializedHost.contains(flowEventbusInitializerClassName)) {
                Log.e(TAG, "已经初始化过了 $flowEventbusInitializerClassName")
                return
            }
            try {
                val clazz = Class.forName(flowEventbusInitializerClassName)
                clazz.getDeclaredMethod(INIT_METHOD).invoke(clazz.kotlin.objectInstance)
                initializedHost.add(flowEventbusInitializerClassName)
                Log.d(TAG, "初始化成功 $flowEventbusInitializerClassName")
                return
            } catch (e: Exception) {
            }
        }
        Log.e(TAG, "初始化失败")
    }
}