package com.like.floweventbus_compiler

import com.like.floweventbus_annotations.BusObserver
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import java.io.IOException
import javax.lang.model.element.Modifier

/*
//  This codes are generated automatically by FlowEventBus. Do not modify!
package xxx

import com.like.floweventbus.EventManager;
import com.like.floweventbus.Initializer;

public class FlowEventBusInitializer implements Initializer {
    @Override
    public void init() {
        EventManager.addEvent(hostClass,
                tag, requestCode, paramType, false, (host, data) -> {
                    ((hostClass) host).observer4((paramType) data);
                    return null;
                });
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
            JavaFile.builder(packageName, createClass(className, methodInfoList))
                .addFileComment(" This codes are generated automatically by FlowEventBus. Do not modify!")// 类的注释
                .build()
                .writeTo(filer)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /*
     * 创建类
     * public class FlowEventBusInitializer implements Initializer {}
     */
    private fun createClass(className: String, methodInfoList: List<MethodInfo>): TypeSpec {
        return TypeSpec.classBuilder(className)
            .addSuperinterface(Class.forName("com.like.floweventbus.Initializer"))
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addMethod(createInitializeFun(methodInfoList))
            .build()
    }

    /*
     * 创建方法
     * @Override
     * public void init() {}
     */
    private fun createInitializeFun(methodInfoList: List<MethodInfo>): MethodSpec {
        return MethodSpec.methodBuilder("init")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override::class.java)
            .addStatement(createCodeBlock(methodInfoList))
            .build()
    }

    /*
     * 创建方法的代码
     *
        EventManager.addEvent(hostClass,
            tag, requestCode, paramType, false, (host, data) -> {
                ((hostClass) host).observer4((paramType) data);
                return null;
            });
        ……
     */
    private fun createCodeBlock(methodInfoList: List<MethodInfo>): CodeBlock {
        val builder = CodeBlock.builder()
        methodInfoList.groupBy { it.hostClass }.forEach { entry ->
            val hostClass = entry.key
            val hostMethodInfoList = entry.value
            hostMethodInfoList.forEach { hostMethodInfo ->
                hostMethodInfo.tags.forEach { tag ->
                    val requestCode = hostMethodInfo.requestCode
                    val paramType = hostMethodInfo.paramType
                    val isSticky = methodInfoList.any {
                        it.tags.contains(tag) && it.requestCode == requestCode && it.isSticky
                    }
                    builder.add(
                        "com.like.floweventbus.EventManager.addEvent(\$S, \$S, \$S, \$S, \$L, (host, data) -> {",
                        hostClass,
                        tag,
                        requestCode,
                        hostMethodInfo.paramType,
                        isSticky.toString()
                    )
                    builder.add(
                        "((\$T) host).${hostMethodInfo.methodName}(${if (paramType == "com.like.floweventbus.NoArgs") "" else "(${hostMethodInfo.paramType}) data"});",
                        hostClass
                    )
                    builder.add("return null;")
                    builder.add("});")
                }
            }
        }
        return builder.build()
    }

}
