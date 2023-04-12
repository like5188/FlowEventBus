package com.like.floweventbus

import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * Flow 管理
 */
object FlowManager {
    private val mFlowCache = mutableMapOf<String, MutableSharedFlow<Any?>>()

    fun findFlowOrCreateIfAbsent(
        tag: String,
        requestCode: String,
        isSticky: Boolean,
        paramType: String,
        isNullable: Boolean,
    ): MutableSharedFlow<Any?> {
        val key = "$tag-$requestCode-$isSticky-$paramType-$isNullable"
        var flow = mFlowCache[key]
        if (flow == null) {
            flow = MutableSharedFlow<Any?>(
                replay = if (isSticky) 1 else 0,
                extraBufferCapacity = if (isSticky) Int.MAX_VALUE else 0 // 避免挂起导致数据发送失败
            ).apply {
                mFlowCache[key] = this
            }
        }
        return flow
    }

}
