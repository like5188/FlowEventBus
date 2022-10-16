package com.like.floweventbus

import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * Flow 管理
 */
object FlowManager {
    private val mFlowCache = mutableMapOf<String, MutableSharedFlow<Any?>>()

    private fun findFlow(
        tag: String,
        requestCode: String,
        isSticky: Boolean,
        paramType: String,
        isNullable: Boolean,
    ): MutableSharedFlow<Any?>? {
        val key = createKey(tag, requestCode, isSticky, paramType, isNullable)
        if (mFlowCache.containsKey(key)) {
            return mFlowCache[key]
        }
        return null
    }

    fun findFlowOrCreateIfAbsent(
        tag: String,
        requestCode: String,
        isSticky: Boolean,
        paramType: String,
        isNullable: Boolean,
    ): MutableSharedFlow<Any?> {
        var flow = findFlow(tag, requestCode, isSticky, paramType, isNullable)
        if (flow == null) {
            flow = MutableSharedFlow<Any?>(
                replay = if (isSticky) 1 else 0,
                extraBufferCapacity = if (isSticky) Int.MAX_VALUE else 0 // 避免挂起导致数据发送失败
            ).apply {
                val key = createKey(tag, requestCode, isSticky, paramType, isNullable)
                mFlowCache[key] = this
            }
        }
        return flow
    }

    private fun createKey(
        tag: String,
        requestCode: String,
        isSticky: Boolean,
        paramType: String,
        isNullable: Boolean,
    ) = "$tag-$requestCode-$isSticky-$paramType-$isNullable"

}
