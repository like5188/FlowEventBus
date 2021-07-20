package com.like.livedatabus_compiler

import javax.lang.model.type.TypeMirror

/**
 * 被@BusObserver注解标注的方法的相关信息
 */
class MethodInfo {
    var methodName = ""// 被@BusObserver注解标注的方法名字
    var tag: Array<String>? = null// 标签
    var requestCode = ""// 请求码
    var isSticky = false// 是否粘性消息
    var paramType: TypeMirror? = null// 被@BusObserver注解标注的方法的参数类型。只支持一个参数
}