package com.like.floweventbus_compiler

import com.like.floweventbus_annotations.BusObserver
import com.squareup.kotlinpoet.*
import java.io.IOException

/*
object FlowEventbusMethods {
    init {
        val methodInfoList = mutableListOf<MethodInfo>()
        for (methodInfo in methodInfoList) {
            for (tag in methodInfo.tags) {
                val isStickyMethod = methodInfoList.any { it.tags.contains(tag) && it.requestCode == methodInfo.requestCode && it.isSticky }
                EventManager.addEvent(
                    methodInfo.hostClass.javaPrimitiveTypeToKotlin(),
                    tag,
                    methodInfo.requestCode,
                    methodInfo.paramType.javaPrimitiveTypeToKotlin(),
                    isStickyMethod
                ) { host, data ->

                }
            }
        }
    }
}
 */
/**
 * [BusObserver]注解的方法缓存类的代码。
 */
object RealClassGenerator {
    private const val PACKAGE_NAME = "com.like.floweventbus"
    private const val CLASS_NAME = "FlowEventbusMethods"

    fun create(methodInfoList: List<MethodInfo>) {
        val filer = ProcessUtils.mFiler ?: return
        try {
            // 创建包名及类的注释
            FileSpec.builder(PACKAGE_NAME, CLASS_NAME)
                .addComment(" This codes are generated automatically by FlowEventBus. Do not modify!")// 类的注释
                .addType(createClass(methodInfoList))
                .build()
                .writeTo(filer)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /*
     * 创建类
     *
     * object FlowEventbusMethods {}
     */
    private fun createClass(methodInfoList: List<MethodInfo>): TypeSpec {
        return TypeSpec.objectBuilder(CLASS_NAME)
            .addProperty(createInitializedProperty())
            .addFunction(createInitializeFun(methodInfoList))
            .build()
    }

    private fun createInitializedProperty(): PropertySpec {
        return PropertySpec.builder("initialized", Boolean::class, KModifier.PRIVATE)
            .initializer("%L", false)
            .mutable(true)
            .build()
    }

    private fun createInitializeFun(methodInfoList: List<MethodInfo>): FunSpec {
        return FunSpec.builder("initialize")
            .addCode(createInitializerBlock(methodInfoList))
            .build()
    }

    private fun createInitializerBlock(methodInfoList: List<MethodInfo>): CodeBlock {
        return buildCodeBlock {
            addStatement("if (initialized) { return }")
            addStatement("initialized = true")
            methodInfoList.groupBy { it.hostClass }.forEach { entry ->
                val hostClass = entry.key
                val hostMethodInfoList = entry.value
                hostMethodInfoList.forEach { hostMethodInfo ->
                    hostMethodInfo.tags.forEach { tag ->
                        val requestCode = hostMethodInfo.requestCode
                        val paramType = hostMethodInfo.paramType
                        val isStickyMethod = methodInfoList.any {
                            it.tags.contains(tag) && it.requestCode == requestCode && it.isSticky
                        }
                        addStatement(
                            "%T.addEvent(%S, %S, %S, %S, %L) { host, data ->",
                            ClassName("com.like.floweventbus", "EventManager"),
                            hostClass,
                            tag,
                            requestCode,
                            hostMethodInfo.getJavaBoxParamType(),
                            isStickyMethod
                        )
                        addStatement(
                            "(host as %T).${hostMethodInfo.methodName}(${if (paramType == "com.like.floweventbus.NoArgs") "" else "data as ${hostMethodInfo.getKotlinParamType()}"});",
                            hostClass.asClassName()
                        )
                        addStatement("}")
                    }
                }
            }
        }
    }

}
