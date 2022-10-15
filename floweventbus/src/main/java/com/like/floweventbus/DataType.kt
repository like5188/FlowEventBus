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
    return canonicalName?.toJavaDataType(isNullable) ?: ""
}

fun String.toJavaDataType(isNullable: Boolean): String {
    return when {
        this == "java.lang.Byte" && !isNullable -> "byte"
        this == "java.lang.Short" && !isNullable -> "short"
        this == "java.lang.Integer" && !isNullable -> "int"
        this == "java.lang.Long" && !isNullable -> "long"
        this == "java.lang.Float" && !isNullable -> "float"
        this == "java.lang.Double" && !isNullable -> "double"
        this == "java.lang.Character" && !isNullable -> "char"
        this == "java.lang.Boolean" && !isNullable -> "boolean"
        else -> this
    }
}

/**
 * [event]和提供的[paramType]是否匹配
 */
fun isParamCompat(paramType: String, event: Event): Boolean {
    // 注意：可空类型可以接受不可空的值。
    return when (paramType) {// 其实 flowNullable 不可能存在下面8种java基本数据类型的。
        "byte" -> event.paramType == "byte" || event.paramType == "java.lang.Byte"
        "short" -> event.paramType == "short" || event.paramType == "java.lang.Short"
        "int" -> event.paramType == "int" || event.paramType == "java.lang.Integer"
        "long" -> event.paramType == "long" || event.paramType == "java.lang.Long"
        "float" -> event.paramType == "float" || event.paramType == "java.lang.Float"
        "double" -> event.paramType == "double" || event.paramType == "java.lang.Double"
        "char" -> event.paramType == "char" || event.paramType == "java.lang.Character"
        "boolean" -> event.paramType == "boolean" || event.paramType == "java.lang.Boolean"
        else -> event.paramType == paramType
    }
}
