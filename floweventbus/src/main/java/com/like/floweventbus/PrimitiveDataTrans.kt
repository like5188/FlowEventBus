package com.like.floweventbus

/**
 * 因为 executableElement.parameters[0].asType()中，
 * 对于 kotlin 代码中的数据类型：
 * Int?会转换成java.lang.Integer；
 * Int会转换成int；
 */
//@Throws(ClassNotFoundException::class)
//fun String.toJavaClass(): Class<*> {
//    return when (this) {
//        "boolean" -> Class.forName("java.lang.Boolean")
//        "byte" -> Class.forName("java.lang.Byte")
//        "short" -> Class.forName("java.lang.Short")
//        "int" -> Class.forName("java.lang.Integer")
//        "long" -> Class.forName("java.lang.Long")
//        "char" -> Class.forName("java.lang.Character")
//        "float" -> Class.forName("java.lang.Float")
//        "double" -> Class.forName("java.lang.Double")
//        else -> Class.forName(this)
//    }
//}
/**
 * AbstractProcessor中，
 * 对于 kotlin 代码中的数据类型：
 * Int? 会转换成 java.lang.Integer；
 * Int 会转换成 int (Class.forName() 方法对于这些基本数据类型会报错)；
 */
@Throws(ClassNotFoundException::class)
fun String.toJavaClassForReflect(): Class<*> {
    return when (this) {
        "boolean" -> Boolean::class.java
        "byte" -> Byte::class.java
        "short" -> Short::class.java
        "int" -> Int::class.java
        "long" -> Long::class.java
        "char" -> Character::class.java
        "float" -> Float::class.java
        "double" -> Double::class.java
        else -> Class.forName(this)
    }
}