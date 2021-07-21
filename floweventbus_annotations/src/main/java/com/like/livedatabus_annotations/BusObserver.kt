package com.like.livedatabus_annotations

// @Retention: 定义注解的保留策略。
// SOURCE：注解仅存在于源码中，在class字节码文件中不包含
// CLASS：默认的保留策略，注解会在class字节码文件中存在，但运行时无法获得，
// RUNTIME：注解会在class字节码文件中存在，在运行时可以通过反射获取到
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
annotation class BusObserver(
        val value: Array<String>,
        val requestCode: String = "",
        val isSticky: Boolean = false
)
