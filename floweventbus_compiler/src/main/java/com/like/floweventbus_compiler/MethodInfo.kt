package com.like.floweventbus_compiler

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
    /*
     * ClassGenerator 中使用方法 executableElement.parameters[0].asType() 获取此值时，
     * 对于 kotlin 代码中的基本数据类型：
     * Int? 会转换成 java.lang.Integer；
     * Int 会转换成 int；
     */
    val paramType: String // 被@BusObserver注解标注的方法的参数类型。只支持一个参数。
) {
    /**
     * 把 java 中的基本数据类型转换成装箱类型。
     *
     * 因为使用 kotlin 代码发送数据时，T::class.java 会自动装箱，所以为了对比，需要使用此方法进行转换
     */
    @Throws(ClassNotFoundException::class)
    fun getJavaBoxParamType(): String {
        return when (paramType) {
            "boolean" -> "java.lang.Boolean"
            "byte" -> "java.lang.Byte"
            "short" -> "java.lang.Short"
            "int" -> "java.lang.Integer"
            "long" -> "java.lang.Long"
            "char" -> "java.lang.Character"
            "float" -> "java.lang.Float"
            "double" -> "java.lang.Double"
            else -> paramType
        }
    }

    /**
     * 在调用 kotlin 方法时，需要还原，才能匹配 kotlin 方法
     */
    @Throws(ClassNotFoundException::class)
    fun getKotlinParamType(): String {
        return when (paramType) {
            "boolean" -> "Boolean"
            "java.lang.Boolean" -> "Boolean?"
            "byte" -> "Byte"
            "java.lang.Byte" -> "Byte?"
            "short" -> "Short"
            "java.lang.Short" -> "Short?"
            "int" -> "Int"
            "java.lang.Integer" -> "Int?"
            "long" -> "Long"
            "java.lang.Long" -> "Long?"
            "char" -> "Char"
            "java.lang.Character" -> "Char?"
            "float" -> "Float"
            "java.lang.Float" -> "Float?"
            "double" -> "Double"
            "java.lang.Double" -> "Double?"
            "java.lang.String" -> "String?"
            else -> paramType
        }
    }
}