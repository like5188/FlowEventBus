package com.like.floweventbus

/**
 * 被@BusObserver注解标注的方法的相关信息
 */
data class MethodInfo(
    val hostClass: String,// 宿主类
    val methodName: String,// 被@BusObserver注解标注的方法名字
    val tags: List<String>,// 标签
    val requestCode: String = "",// 请求码
    val isSticky: Boolean = false,// 是否粘性消息
    val paramType: String? = null // 被@BusObserver注解标注的方法的参数类型。只支持一个参数
)