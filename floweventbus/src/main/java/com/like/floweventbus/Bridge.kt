package com.like.floweventbus

import android.util.Log
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * 自动生成的代码与本模块交互的桥梁
 */
object Bridge {
    val mEventList = mutableListOf<Event>()

    /**
     * 由自动生成的代码来调用
     */
    fun addEvent(
        hostClass: String,
        tag: String,
        requestCode: String,
        paramType: String,
        isStickyMethod: Boolean,
        callback: (Any, Any?) -> Unit
    ) {
        val flow = mEventList.firstOrNull {
            it.flow.tag == tag && it.flow.requestCode == requestCode && it.flow.paramType == paramType
        }?.flow ?: FlowWrapper(
            tag, requestCode, paramType, MutableSharedFlow(
                replay = if (isStickyMethod) 1 else 0,
                extraBufferCapacity = Int.MAX_VALUE // 避免挂起导致数据发送失败
            )
        )
        with(Event(hostClass, flow, callback)) {
            mEventList.add(this)
            Log.i(TAG, "添加事件 --> $this")
            EventManager.logEvent()
        }
    }

    /**
     * 处理注解，注册的时候调用
     */
    internal fun handleAnnotations() {
        try {
            val clazz = Class.forName("com.like.floweventbus.FlowEventbusMethods")
            clazz.getDeclaredMethod("initialize").invoke(clazz.kotlin.objectInstance)
        } catch (e: Exception) {
            Log.e(TAG, "处理注解信息失败 --> ${e.message}")
        }
    }
}