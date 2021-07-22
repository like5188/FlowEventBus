package com.like.floweventbus_compiler

import com.google.gson.*
import com.like.floweventbus_annotations.BusObserver
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
import java.io.IOException
import java.lang.reflect.Type
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror

/*
public class MainViewModel_Methods {
  public static String METHODS = "";
}
 */
/**
 * [BusObserver]注解的方法缓存类的代码。
 */
object MethodsCacheClassGenerator {
    private const val CLASS_UNIFORM_MARK = "_Methods"

    // 因为java工程中没有下面这些类(Android中的类)，所以只能采用ClassName的方式。
    private val STRING = ClassName.get("java.lang", "String")

    fun create(hostClass: TypeElement, methodInfoList: List<MethodInfo>) {
        try {
            // 创建包名及类的注释
            JavaFile.builder(ClassName.get(hostClass).packageName(), createClass(hostClass, methodInfoList))
                .addFileComment(" This codes are generated automatically by FlowEventBus. Do not modify!")// 类的注释
                .build()
                .writeTo(ProcessUtils.mFiler)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * 创建类
     *
     * public class MainViewModel_Methods {}
     */
    private fun createClass(hostClass: TypeElement, methodInfoList: List<MethodInfo>): TypeSpec {
        return TypeSpec.classBuilder(ClassName.get(hostClass).simpleName() + CLASS_UNIFORM_MARK)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addField(createMethodsField(methodInfoList))
            .build()
    }

    /**
     * 创建 methods 属性
     *
     * public static String METHODS = "";
     */
    private fun createMethodsField(methodInfoList: List<MethodInfo>): FieldSpec {
        val gson = GsonBuilder()
            .registerTypeAdapter(TypeElement::class.java, object : JsonSerializer<TypeElement> {
                override fun serialize(src: TypeElement?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
                    return JsonPrimitive(src?.toString() ?: "")
                }
            })
            .registerTypeAdapter(TypeMirror::class.java, object : JsonSerializer<TypeMirror> {
                override fun serialize(src: TypeMirror?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
                    return JsonPrimitive(src?.toString() ?: "")
                }
            })
            .create()
        return FieldSpec.builder(STRING, "METHODS", Modifier.PUBLIC, Modifier.STATIC)
            .initializer("\$S", gson.toJson(methodInfoList))
            .build()
    }

}
