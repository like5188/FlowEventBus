package com.like.floweventbus_compiler

import com.google.gson.*
import com.like.floweventbus_annotations.BusObserver
import com.squareup.javapoet.*
import java.io.IOException
import java.lang.reflect.Type
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement

/*
public class FlowEventbusMethods {
  public static String xxx_METHODS = "";
}
 */
/**
 * [BusObserver]注解的方法缓存类的代码。
 */
object MethodsCacheClassGenerator {
    private const val PACKAGE_NAME = "com.like.floweventbus_compiler"
    private const val CLASS_NAME = "FlowEventbusMethods"
    private const val FIELD_SUFFIX = "_METHODS"

    // 因为java工程中没有下面这些类(Android中的类)，所以只能采用ClassName的方式。
    private val STRING: ClassName = ClassName.get("java.lang", "String")

    private val mGson = GsonBuilder()
        .registerTypeAdapter(TypeElement::class.java, object : JsonSerializer<TypeElement> {
            override fun serialize(src: TypeElement?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
                return JsonPrimitive(src?.toString() ?: "")
            }
        })
        .registerTypeAdapter(TypeName::class.java, object : JsonSerializer<TypeName> {
            override fun serialize(src: TypeName?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
                return JsonPrimitive(src?.toString() ?: "")
            }
        })
        .create()

    fun create(methodInfoList: List<MethodInfo>) {
        try {
            // 创建包名及类的注释
            JavaFile.builder(PACKAGE_NAME, createClass(methodInfoList))
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
     * public class FlowEventbusMethods {}
     */
    private fun createClass(methodInfoList: List<MethodInfo>): TypeSpec {
        val builder = TypeSpec.classBuilder(CLASS_NAME)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
        methodInfoList.groupBy { it.hostClass }.forEach {
            val hostClass = it.key
            val methods = it.value
            builder.addField(createMethodsField(hostClass, methods))
        }
        return builder.build()
    }

    /**
     * 创建 methods 属性
     *
     * public static String xxx_METHODS = "";
     */
    private fun createMethodsField(hostClass: TypeElement, methods: List<MethodInfo>): FieldSpec {
        return FieldSpec.builder(
            STRING,
            "${hostClass.qualifiedName.toString().replace(".", "_")}$FIELD_SUFFIX",
            Modifier.PUBLIC,
            Modifier.STATIC
        )
            .initializer("\$S", mGson.toJson(methods))
            .build()
    }

}
