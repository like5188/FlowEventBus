package com.like.floweventbus

/**
 * 把 kotlin 中的非空基本数据类型转换成 java 中的基本数据类型装箱类型。
 *
 * 因为使用 kotlin 代码发送数据时，T::class.java 会自动装箱
 */
@Throws(ClassNotFoundException::class)
fun Class<*>.box(): Class<*> {
    return when (this) {
        Boolean::class.java -> Class.forName("java.lang.Boolean")
        Byte::class.java -> Class.forName("java.lang.Byte")
        Short::class.java -> Class.forName("java.lang.Short")
        Int::class.java -> Class.forName("java.lang.Integer")
        Long::class.java -> Class.forName("java.lang.Long")
        Char::class.java -> Class.forName("java.lang.Character")
        Float::class.java -> Class.forName("java.lang.Float")
        Double::class.java -> Class.forName("java.lang.Double")
        else -> this
    }
}

/**
 * 把 java 中的基本数据类型转换成 kotlin 中的非空基本数据类型。
 * 因为 Class.forName() 方法对于 java 中的基本数据类型会报错。
 * 而 AbstractProcessor 中，
 * 对于 kotlin 代码中的数据类型：
 * Int? 会转换成 java.lang.Integer；
 * Int 会转换成 int；
 *
 * 在使用反射获取方法时，填入的参数类型：
 * Int->getDeclaredMethod(methodName, Int::class.java)
 * Int?->getDeclaredMethod(methodName, Class.forName("java.lang.Integer"))
 */
@Throws(ClassNotFoundException::class)
fun String.javaPrimitiveTypeToKotlin(): Class<*> {
    return when (this) {
        "boolean" -> Boolean::class.java
        "byte" -> Byte::class.java
        "short" -> Short::class.java
        "int" -> Int::class.java
        "long" -> Long::class.java
        "char" -> Char::class.java
        "float" -> Float::class.java
        "double" -> Double::class.java
        else -> Class.forName(this)
    }
}
