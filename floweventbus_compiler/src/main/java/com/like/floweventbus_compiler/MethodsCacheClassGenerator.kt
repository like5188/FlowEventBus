package com.like.floweventbus_compiler

import com.google.gson.*
import com.like.floweventbus_annotations.BusObserver
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import java.io.IOException
import java.lang.reflect.Type
import javax.lang.model.element.TypeElement

/*
object FlowEventbusMethods {
    const val com_like_floweventbus_sample_BaseActivity1_METHODS_METHODS = ""
    const val com_like_floweventbus_sample_MainActivity_METHODS = ""
}
 */
/**
 * [BusObserver]注解的方法缓存类的代码。
 */
object MethodsCacheClassGenerator {
    private const val PACKAGE_NAME = "com.like.floweventbus_compiler"
    private const val CLASS_NAME = "FlowEventbusMethods"
    private const val FIELD_SUFFIX = "_METHODS"

    private val mGson = GsonBuilder()
        .registerTypeAdapter(TypeElement::class.java, object : JsonSerializer<TypeElement> {
            override fun serialize(src: TypeElement?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
                return JsonPrimitive(src?.toString() ?: "")
            }
        })
        .create()

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

    /**
     * 创建类
     *
     * object FlowEventbusMethods {}
     */
    private fun createClass(methodInfoList: List<MethodInfo>): TypeSpec {
        val builder = TypeSpec.objectBuilder(CLASS_NAME)
        methodInfoList.groupBy { it.hostClass }.forEach {
            val hostClass = it.key
            val methods = it.value
            builder.addProperty(createMethodsProperty(hostClass, methods))
        }
        return builder.build()
    }

    /**
     * 创建 methods 属性
     *
     * const val com_like_floweventbus_sample_BaseActivity1_METHODS_METHODS = ""
     */
    private fun createMethodsProperty(hostClass: String, methods: List<MethodInfo>): PropertySpec {
        val propertyName = "${hostClass.replace(".", "_")}$FIELD_SUFFIX"
        return PropertySpec.builder(propertyName, String::class)
            .addModifiers(KModifier.CONST)
            .initializer("%S", mGson.toJson(methods))
            .build()
    }

}
