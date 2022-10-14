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
    return if (isNullable) {
        event.flow.paramType == paramType && event.flow.isNullable == isNullable
    } else {
        when (paramType) {
            "byte" -> event.flow.paramType == "byte" || event.flow.paramType == "java.lang.Byte"
            "short" -> event.flow.paramType == "short" || event.flow.paramType == "java.lang.Short"
            "int" -> event.flow.paramType == "int" || event.flow.paramType == "java.lang.Integer"
            "long" -> event.flow.paramType == "long" || event.flow.paramType == "java.lang.Long"
            "float" -> event.flow.paramType == "float" || event.flow.paramType == "java.lang.Float"
            "double" -> event.flow.paramType == "double" || event.flow.paramType == "java.lang.Double"
            "char" -> event.flow.paramType == "char" || event.flow.paramType == "java.lang.Character"
            "boolean" -> event.flow.paramType == "boolean" || event.flow.paramType == "java.lang.Boolean"
            else -> event.flow.paramType == paramType
        }
    }
}
