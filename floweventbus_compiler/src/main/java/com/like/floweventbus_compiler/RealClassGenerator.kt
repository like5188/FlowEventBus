package com.like.floweventbus_compiler

import com.like.floweventbus_annotations.BusObserver
import com.squareup.kotlinpoet.*
import java.io.IOException
import javax.lang.model.element.TypeElement

/*
object FlowEventbusCompiler {
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
    fun create(methodInfoList: List<MethodInfo>) {
        val filer = ProcessUtils.mFiler ?: return
        methodInfoList.groupBy { it.hostClass }.forEach {
            val hostClass = it.key
            val hostMethodInfoList = it.value
            val packageName = hostClass.asClassName().packageName
            val className = "${hostClass.qualifiedName.toString().replace(".", "_")}_methods"
            try {
                // 创建包名及类的注释
                FileSpec.builder(packageName, className)
                    .addComment(" This codes are generated automatically by FlowEventBus. Do not modify!")// 类的注释
                    .addType(createClass(hostClass, className, methodInfoList, hostMethodInfoList))
                    .build()
                    .writeTo(filer)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /*
     * 创建类
     *
     * object FlowEventbusCompiler {}
     */
    private fun createClass(
        hostClass: TypeElement,
        className: String,
        methodInfoList: List<MethodInfo>,
        hostMethodInfoList: List<MethodInfo>
    ): TypeSpec {
        val builder = TypeSpec.objectBuilder(className)
            .addInitializerBlock(createInitializerBlock(hostClass, methodInfoList, hostMethodInfoList))
        return builder.build()
    }

    private fun createInitializerBlock(
        hostClass: TypeElement,
        methodInfoList: List<MethodInfo>,
        hostMethodInfoList: List<MethodInfo>
    ): CodeBlock {
        return buildCodeBlock {
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
