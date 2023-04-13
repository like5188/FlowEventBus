package com.like.floweventbus.compiler

import com.like.floweventbus.annotations.BusObserver
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement

/*
在kotlinpoet中，每一个节点都对应一个Spec
类对象 					说明

MethodSpec 			代表一个构造函数或方法声明
TypeSpec 			代表一个类，接口，或者枚举声明
FieldSpec 			代表一个成员变量，一个字段声明
JavaFile 			包含一个顶级类的Java文件
ParameterSpec 		用来创建参数
AnnotationSpec 		用来创建注解
ClassName 			用来包装一个类
TypeName 			类型，如在添加返回值类型是使用 TypeName.VOID

通配符：
%S 字符串，如：%S, ”hello”
%T 类、接口，如：%T, MainActivity

 */
internal class Generator {
    companion object {
        /**
         * 需要自动生成的类的类名
         */
        private const val CLASS_NAME = "FlowEventBusInitializer"

        /**
         * 需要自动生成的配置文件的文件名
         */
        private const val META_INF_SERVICES_FILE_NAME = "com.like.floweventbus.Initializer"
    }

    private val mMethodInfoList = mutableListOf<MethodInfo>()// 类中的所有方法

    fun generate(packageName: String) {
        if (mMethodInfoList.isEmpty()) {
            return
        }
        ClassGenerator.generate(packageName, CLASS_NAME, mMethodInfoList)
        FileGenerator.generateConfigFiles(META_INF_SERVICES_FILE_NAME, "$packageName.$CLASS_NAME")
    }

    /**
     * 添加元素，用于生成类
     */
    fun addElement(element: Element) {
        val method = (element as? ExecutableElement) ?: return
        val hostClass = (method.enclosingElement as? TypeElement) ?: return

        val methodName = method.simpleName.toString()
        if (methodName.isEmpty()) return

        val annotation = method.getAnnotation(BusObserver::class.java)
        val tags = annotation.value.toList().filter { it.isNotEmpty() }.distinct()
        if (tags.isEmpty()) return

        val requestCode = annotation.requestCode
        val isSticky = annotation.isSticky

        val isNullable: Boolean
        val paramType: String
        when (method.parameters.size) {
            0 -> {
                paramType = "com.like.floweventbus.NoArgs"// 用于注解的方法没有参数时的处理
                isNullable = false
            }
            1 -> {
                val variableElement = method.parameters[0]
                paramType = variableElement.asType().toString()
                isNullable = variableElement.annotationMirrors.any {
                    it.annotationType.toString() == "org.jetbrains.annotations.Nullable"
                }
            }
            else -> return
        }
        mMethodInfoList.add(
            MethodInfo(hostClass, methodName, tags, requestCode, isSticky, paramType, isNullable)
        )
    }

}
