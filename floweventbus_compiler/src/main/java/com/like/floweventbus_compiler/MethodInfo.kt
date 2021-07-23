package com.like.floweventbus_compiler

import com.squareup.kotlinpoet.TypeName
import javax.lang.model.element.TypeElement

/**
 * 被@BusObserver注解标注的方法的相关信息
 */
data class MethodInfo(
    val hostClass: TypeElement,// 宿主类
    val methodName: String,// 被@BusObserver注解标注的方法名字
    val tags: List<String>,// 标签
    val requestCode: String,// 请求码
    val isSticky: Boolean,// 是否粘性消息
    val paramType: TypeName // 被@BusObserver注解标注的方法的参数类型。只支持一个参数
)