package com.like.floweventbus

import kotlin.reflect.typeOf

/**
 * 将 kotlin 数据类型[T]转换成 java 数据类型。
 * （只是对 java 基本数据类型做了处理，为了和[com.like.floweventbus_compiler.Generator]中存储的参数类型一样）
 * 比如：
 * java.lang.Byte 不为空 -> byte
 * java.lang.Byte 为空  -> java.lang.Byte
 */
inline fun <reified T> toJavaDataType(): String {
    val isNullable = typeOf<T>().isMarkedNullable
    val canonicalName = T::class.java.canonicalName
    return when {
        canonicalName == "java.lang.Byte" && !isNullable -> "byte"
        canonicalName == "java.lang.Short" && !isNullable -> "short"
        canonicalName == "java.lang.Integer" && !isNullable -> "int"
        canonicalName == "java.lang.Long" && !isNullable -> "long"
        canonicalName == "java.lang.Float" && !isNullable -> "float"
        canonicalName == "java.lang.Double" && !isNullable -> "double"
        canonicalName == "java.lang.Character" && !isNullable -> "char"
        canonicalName == "java.lang.Boolean" && !isNullable -> "boolean"
        else -> canonicalName ?: ""
    }
}

/**
 * [event]是否和提供的[paramType]+[isNullable]是否匹配
 */
fun isParamCompat(paramType: String, isNullable: Boolean, event: Event): Boolean {
    // 注意：可空类型可以接受不可空的值。
    val flow = (if (isNullable) event.flowNullable else event.flowNotNull) ?: return false
    return when (paramType) {// 其实 flowNullable 不可能存在下面8种java基本数据类型的。
        "byte" -> flow.paramType == "byte" || flow.paramType == "java.lang.Byte"
        "short" -> flow.paramType == "short" || flow.paramType == "java.lang.Short"
        "int" -> flow.paramType == "int" || flow.paramType == "java.lang.Integer"
        "long" -> flow.paramType == "long" || flow.paramType == "java.lang.Long"
        "float" -> flow.paramType == "float" || flow.paramType == "java.lang.Float"
        "double" -> flow.paramType == "double" || flow.paramType == "java.lang.Double"
        "char" -> flow.paramType == "char" || flow.paramType == "java.lang.Character"
        "boolean" -> flow.paramType == "boolean" || flow.paramType == "java.lang.Boolean"
        else -> flow.paramType == paramType
    }
}
