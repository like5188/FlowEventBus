package com.like.floweventbus

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect

/**
 * Flow 由 tag、requestCode、paramType 组合决定
 */
data class FlowWrapper<T>(
    val tag: String,// 标签
    val requestCode: String,// 请求码。当标签相同时，可以使用请求码区分
    val paramType: String,// 被@BusObserver注解标注的方法的参数类型。只支持一个参数
    val flow: MutableSharedFlow<T>,
) {

    suspend inline fun collect(crossinline action: suspend (value: T) -> Unit) {
        flow.collect(action)
    }

    suspend fun emit(value: T) {
        flow.emit(value)
    }

    override fun toString(): String {
        return "FlowWrapper(tag='$tag', ${if (requestCode.isNotEmpty()) ", requestCode='$requestCode'" else ""}, paramType=$paramType)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FlowWrapper<*>) return false

        if (tag != other.tag) return false
        if (requestCode != other.requestCode) return false
        if (paramType != other.paramType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = tag.hashCode()
        result = 31 * result + requestCode.hashCode()
        result = 31 * result + paramType.hashCode()
        return result
    }

}
