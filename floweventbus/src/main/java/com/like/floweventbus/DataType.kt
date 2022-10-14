package com.like.floweventbus

import kotlin.reflect.typeOf

/**
 * 将 kotlin 数据类型[T]转换成 java 数据类型。
 * （只是对 java 基本数据类型做了处理，为了和 com.like.floweventbus_compiler.Generator 中存储的参数类型一样）
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