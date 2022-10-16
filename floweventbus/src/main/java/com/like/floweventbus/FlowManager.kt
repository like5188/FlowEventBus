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
        paramType: String,
        isNullable: Boolean,
        isSticky: Boolean,
    ): MutableSharedFlow<Any?>? {
        val key = createKey(tag, requestCode, paramType, isNullable, isSticky)
        if (mFlowCache.containsKey(key)) {
            return mFlowCache[key]
        }
        return null
    }

    fun findFlowOrCreateIfAbsent(
        tag: String,
        requestCode: String,
        paramType: String,
        isNullable: Boolean,
        isSticky: Boolean,
    ): MutableSharedFlow<Any?> {
        var flow = findFlow(tag, requestCode, paramType, isNullable, isSticky)
        if (flow == null) {
            flow = MutableSharedFlow<Any?>(
                replay = if (isSticky) 1 else 0,
                extraBufferCapacity = if (isSticky) Int.MAX_VALUE else 0 // 避免挂起导致数据发送失败
            ).apply {
                val key = createKey(tag, requestCode, paramType, isNullable, isSticky)
                mFlowCache[key] = this
            }
        }
        return flow
    }

    private fun createKey(
        tag: String,
        requestCode: String,
        paramType: String,
        isNullable: Boolean,
        isSticky: Boolean,
    ) = "$tag-$requestCode-$paramType-$isNullable-$isSticky"

}
