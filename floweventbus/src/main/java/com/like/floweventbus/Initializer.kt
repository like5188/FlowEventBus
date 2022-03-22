package com.like.floweventbus

import android.util.Log
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 初始化工具
 */
class Initializer {
    companion object {
        private const val AUTO_GENERATED_CLASS = "com.like.floweventbus.FlowEventbusMethods"
        private const val INITIALIZE_METHOD = "initialize"
    }

    private var initialized: AtomicBoolean = AtomicBoolean()

    /**
     * 处理注解，注册的时候调用。
     * 此方法会调用 FlowEventbusMethods.initialize() 方法，然后触发 [EventManager.addEvent] 方法。
     */
    fun initialize() {
        if (initialized.compareAndSet(false, true)) {
            try {
                val clazz = Class.forName(AUTO_GENERATED_CLASS)
                clazz.getDeclaredMethod(INITIALIZE_METHOD).invoke(clazz.kotlin.objectInstance)
                Log.e(TAG, "初始化成功")
            } catch (e: Exception) {
                Log.e(TAG, "初始化失败 --> ${e.message}")
            }
        } else {
            Log.e(TAG, "已经初始化过了")
        }
    }
}