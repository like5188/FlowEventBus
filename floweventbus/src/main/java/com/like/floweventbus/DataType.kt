package com.like.floweventbus

import kotlin.reflect.typeOf

inline fun <reified T> toJavaDataType(): String {
    val isNullable = typeOf<T>().isMarkedNullable
    val canonicalName = T::class.java.canonicalName
    return canonicalName?.toJavaDataType(isNullable) ?: ""
}

/**
 * 将 kotlin 数据类型[this]转换成 java 数据类型。
 * 只是对 java 基本数据类型做了处理，把不为空的基本数据类型包装类转换成对应的基本数据类型。
 * 这样才能和[com.like.floweventbus.compiler.Generator]中存储的参数类型一样。
 * 比如：
 * java.lang.Byte 不为空 -> byte
 * java.lang.Byte 为空  -> java.lang.Byte
 */
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
