package com.like.floweventbus_compiler

import com.like.floweventbus_annotations.BusObserver
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement

internal class ClassGenerator {
    companion object {
        val NO_ARGS: ClassName = ClassName.get("com.like.floweventbus", "NoArgs")
    }

    private val mMethodInfoList = mutableListOf<MethodInfo>()// 类中的所有方法

    fun create() {
        if (mMethodInfoList.isEmpty()) {
            return
        }
        MethodsCacheClassGenerator.create(mMethodInfoList)
    }

    /**
     * 添加元素，用于生成类
     */
    fun addMethod(method: Element) {
        val hostClass = method.enclosingElement as? TypeElement
        hostClass ?: return

        val annotation = method.getAnnotation(BusObserver::class.java)

        val tags = annotation.value.toList()
        if (tags.isNullOrEmpty()) return
        tags.forEach {
            if (it.isEmpty()) {
                return
            }
        }

        val requestCode = annotation.requestCode

        val methodName = method.simpleName.toString()
        if (methodName.isEmpty()) return

        val isSticky = annotation.isSticky

        val executableElement = method as ExecutableElement
        var typeName: TypeName = NO_ARGS
        if (executableElement.parameters.size >= 1) {
            val paramType = executableElement.parameters[0].asType()
            if (paramType.kind.isPrimitive) {
                typeName = TypeName.get(paramType)
                if (!TypeName.get(paramType).isBoxedPrimitive) {// 如果不是装箱数据类型
                    typeName = typeName.box()
                }
            } else {
                typeName = ClassName.get(paramType)
            }
        }
        mMethodInfoList.add(
            MethodInfo(hostClass, methodName, tags, requestCode, isSticky, typeName)
        )
    }
}