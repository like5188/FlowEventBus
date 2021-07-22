package com.like.floweventbus_compiler

import com.like.floweventbus_annotations.BusObserver
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror

internal class ClassGenerator {
    private val mMethodInfoList = mutableListOf<MethodInfo>()// 类中的所有方法

    fun create() {
        if (mMethodInfoList.isEmpty()) {
            return
        }
        val hostClass = mMethodInfoList.first().hostClass
//        HostProxyClassGenerator.create(hostClass, mMethodInfoList)
        MethodsCacheClassGenerator.create(hostClass, mMethodInfoList)
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

        val requestCode = annotation.requestCode

        // 判断是否有重复的tag + requestCode
        val isRepeat = mMethodInfoList.any {
            it.tags.intersect(tags).isNotEmpty() && it.requestCode == requestCode
        }
        if (isRepeat) return

        val methodName = method.simpleName.toString()
        if (methodName.isEmpty()) return

        val isSticky = annotation.isSticky

        var paramType: TypeMirror? = null
        val executableElement = method as ExecutableElement
        if (executableElement.parameters.size == 1) {
            paramType = executableElement.parameters[0].asType()
        }
        mMethodInfoList.add(
            MethodInfo(hostClass, methodName, tags, requestCode, isSticky, paramType)
        )
    }
}