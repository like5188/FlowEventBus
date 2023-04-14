package com.like.floweventbus

import kotlin.reflect.typeOf

/**
 * 将 kotlin 数据类型转换成 java 数据类型。
 * 只是对 java 基本数据类型做了处理，把不为空的基本数据类型包装类转换成对应的基本数据类型。
 * 这样才能和[com.like.floweventbus.compiler.Generator]中存储的参数类型一样。
 * 比如：
 * java.lang.Byte 不为空 -> byte
 * java.lang.Byte 为空  -> java.lang.Byte
 */
inline fun <reified T> toJavaDataType(): String {
    val isNullable = typeOf<T>().isMarkedNullable
    val canonicalName = T::class.java.canonicalName
    return canonicalName.toJavaDataType(isNullable)
}

/**
 * 将 kotlin 数据类型[this]转换成 java 数据类型。
 * 只是对 java 基本数据类型做了处理，把不为空的基本数据类型包装类转换成对应的基本数据类型。
 * 这样才能和[com.like.floweventbus.compiler.Generator]中存储的参数类型一样。
 * 比如：
 * java.lang.Byte 不为空 -> byte
 * java.lang.Byte 为空  -> java.lang.Byte
 */
fun String?.toJavaDataType(isNullable: Boolean): String {
    return when {
        this.isNullOrEmpty() -> ""
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
 * 将 java 数据类型转换成 kotlin 数据类型。
 */
fun String?.toKotlinDataType(): Class<*>? {
    return when {
        this.isNullOrEmpty() -> null
        this == "java.lang.Byte" || this == "byte" -> Byte::class.java
        this == "java.lang.Short" || this == "short" -> Short::class.java
        this == "java.lang.Integer" || this == "int" -> Int::class.java
        this == "java.lang.Long" || this == "long" -> Long::class.java
        this == "java.lang.Float" || this == "float" -> Float::class.java
        this == "java.lang.Double" || this == "double" -> Double::class.java
        this == "java.lang.Character" || this == "char" -> Char::class.java
        this == "java.lang.Boolean" || this == "boolean" -> Boolean::class.java
        else -> Class.forName(this)
    }
}
