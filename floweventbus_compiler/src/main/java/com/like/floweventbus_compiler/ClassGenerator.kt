package com.like.floweventbus_compiler

import com.like.floweventbus_annotations.BusObserver
import com.squareup.kotlinpoet.*
import java.io.IOException

/*
//  This codes are generated automatically by FlowEventBus. Do not modify!
package xxx

class FlowEventBusInitializer : Initializer {
    override fun init() {
        EventManager.addEvent("com.like.floweventbus.sample.MainActivity", "like1", "1", "com.like.floweventbus.NoArgs", false) { host, data ->
            (host as MainActivity).observer1();
        }
        ……
    }
}
 */
/**
 * [BusObserver]注解的方法缓存类的代码。
 */
object ClassGenerator {

    fun generate(packageName: String, className: String, methodInfoList: List<MethodInfo>) {
        val filer = ProcessUtils.mFiler ?: return
        try {
            /*
             创建包名及类的注释
             // This codes are generated automatically by FlowEventBus. Do not modify!
             package xxx
             */
            FileSpec.builder(packageName, className)
                .addComment(" This codes are generated automatically by FlowEventBus. Do not modify!")// 类的注释
                .addType(createClass(className, methodInfoList))
                .build()
                .writeTo(filer)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /*
     * 创建类
     * class FlowEventBusInitializer : Initializer {}
     */
    private fun createClass(className: String, methodInfoList: List<MethodInfo>): TypeSpec {
        return TypeSpec.classBuilder(className)
            .addSuperinterface(Class.forName("com.like.floweventbus.Initializer"))
            .addFunction(createInitializeFun(methodInfoList))
            .build()
    }

    /*
     * 创建方法
     *
     * override fun init() {}
     */
    private fun createInitializeFun(methodInfoList: List<MethodInfo>): FunSpec {
        return FunSpec.builder("init")
            .addModifiers(KModifier.OVERRIDE)
            .addCode(createCodeBlock(methodInfoList))
            .build()
    }

    /*
     * 创建方法的代码
     *
        EventManager.addEvent("com.like.floweventbus.sample.MainActivity", "like1", "1", "com.like.floweventbus.NoArgs", false) { host, data ->
            (host as MainActivity).observer1();
        }
        ……
     */
    private fun createCodeBlock(methodInfoList: List<MethodInfo>): CodeBlock {
        return buildCodeBlock {
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
                            "com.like.floweventbus.EventManager.addEvent(%S, %S, %S, %S, %L) { host, data ->",
                            hostClass,
                            tag,
                            requestCode,
                            hostMethodInfo.getJavaBoxParamType(),
                            isStickyMethod
                        )
                        addStatement(
                            "(host as %T).${hostMethodInfo.methodName}(${if (paramType == "com.like.floweventbus.NoArgs") "" else "data as ${hostMethodInfo.getKotlinParamType()}"});",
                            hostClass
                        )
                        addStatement("}")
                    }
                }
            }
        }
    }

}
