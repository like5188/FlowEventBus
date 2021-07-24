package com.like.floweventbus_compiler

import com.like.floweventbus_annotations.BusObserver
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
internal class ClassGenerator {
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
        val paramType = when (executableElement.parameters.size) {
            0 -> "com.like.floweventbus.NoArgs"// 用于注解的方法没有参数时的处理
            /*
             * AbstractProcessor中，
             * 对于 kotlin 代码中的数据类型：
             * Int? 会转换成 java.lang.Integer；
             * Int 会转换成 int；
             */
            1 -> executableElement.parameters[0].asType().toString()
            else -> return
        }
        mMethodInfoList.add(
            MethodInfo(hostClass.qualifiedName.toString(), methodName, tags, requestCode, isSticky, paramType)
        )
    }

}
