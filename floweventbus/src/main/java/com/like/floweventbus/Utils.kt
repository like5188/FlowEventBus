package com.like.floweventbus

@Throws(ClassNotFoundException::class)
fun String.toClass(): Class<*> {
    return when (this) {
        "boolean", "kotlin.Boolean" -> Boolean::class.java
        "byte", "kotlin.Byte" -> Byte::class.java
        "short", "kotlin.Short" -> Short::class.java
        "int", "kotlin.Int" -> Int::class.java
        "long", "kotlin.Long" -> Long::class.java
        "char", "kotlin.Char" -> Char::class.java
        "float", "kotlin.Float" -> Float::class.java
        "double", "kotlin.Double" -> Double::class.java
        else -> Class.forName(this)
    }
}