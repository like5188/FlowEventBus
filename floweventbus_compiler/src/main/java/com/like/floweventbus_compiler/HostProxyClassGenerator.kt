package com.like.floweventbus_compiler

import com.like.floweventbus_annotations.BusObserver
import com.squareup.javapoet.*
import java.io.IOException
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement

/*
public class MainViewModel_Proxy<T extends User> extends HostProxy {
  @Override
  public void subscribeEvent(final Class hostClass, final String tag, final String requestCode, final boolean isSticky) {
    com.like.floweventbus.EventManager.subscribeEvent(
        hostClass
        ,"like5"
        ,""
        ,false);
  }

  @Override
  public void registerHost(final Object host, final LifecycleOwner owner) {
    com.like.floweventbus.EventManager.registerHost(
        host
        ,owner
        ,new Observer<Integer>() {
          @Override
          public void onChanged(Integer t) {
            ((BaseActivity1) host).test1(t);;
          }
        });
  }
}
 */
/**
 * 生成宿主类(包含[BusObserver]注解方法的类)对应的代理类的代码。
 */
object HostProxyClassGenerator {
    private const val CLASS_UNIFORM_MARK = "_Proxy"

    // 因为java工程中没有下面这些类(Android中的类)，所以只能采用ClassName的方式。
    private val HOST_PROXY: ClassName = ClassName.get("com.like.floweventbus", "HostProxy")
    private val OBSERVER: ClassName = ClassName.get("androidx.lifecycle", "Observer")
    private val LIFECYCLE_OWNER: ClassName = ClassName.get("androidx.lifecycle", "LifecycleOwner")
    private val EVENT_MANAGER: ClassName = ClassName.get("com.like.floweventbus", "EventManager")

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
     * public class MainViewModel_Proxy<T extends User> extends HostProxy {}
     */
    private fun createClass(hostClass: TypeElement, methodInfoList: List<MethodInfo>): TypeSpec {
        val builder = TypeSpec.classBuilder(ClassName.get(hostClass).simpleName() + CLASS_UNIFORM_MARK)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .superclass(HOST_PROXY)
            .addMethod(createRegisterHostMethod(hostClass, methodInfoList))
        // 如果宿主类有泛型，也需要添加到代理类中。
        hostClass.typeParameters?.forEach {
            builder.addTypeVariable(TypeVariableName.get(it))
        }
        return builder.build()
    }

    /**
     * 创建 registerHost 方法
     *
     * @Override
     * public void registerHost(final Object host, final LifecycleOwner owner) {}
     */
    private fun createRegisterHostMethod(hostClass: TypeElement, methodInfoList: List<MethodInfo>): MethodSpec {
        val builder = MethodSpec.methodBuilder("registerHost")
            .addModifiers(Modifier.PUBLIC)
            .addParameter(TypeName.OBJECT, "host", Modifier.FINAL)
            .addParameter(LIFECYCLE_OWNER, "owner", Modifier.FINAL)
            .addAnnotation(Override::class.java)
        for (methodInfo in methodInfoList) {
            builder.addCode(createRegisterHostMethodCodeBlock(hostClass, methodInfo))
        }
        return builder.build()
    }

    /**
     * 创建 registerHost 方法中调用的方法
     *
     * com.like.floweventbus.EventManager.register(host, owner, observer)
     */
    private fun createRegisterHostMethodCodeBlock(hostClass: TypeElement, methodInfo: MethodInfo): CodeBlock {
        val builder = CodeBlock.builder()

        val codeBlockBuilder = CodeBlock.builder()
        codeBlockBuilder.addStatement(
            "\$L.registerHost(\nhost\n,owner\n,\$L)",
            EVENT_MANAGER,
            createObserverParam(hostClass, methodInfo)
        )
        builder.add(codeBlockBuilder.build())
        return builder.build()
    }

    /*
     * 创建observe方法的第四个参数observer，是一个匿名内部类。
     *
        new Observer<T>() {
            @Override
            public void onChanged(@Nullable T s) {
                // 调用@BusObserver注解的接收数据的方法
                ((MainViewModel) host).method(s);
            }
        }
     */
    private fun createObserverParam(hostClass: TypeElement, methodInfo: MethodInfo): TypeSpec {
        // 获取onChanged方法的参数类型
        val typeName: TypeName = methodInfo.paramType

        // 创建onChanged方法
        val methodBuilder = MethodSpec.methodBuilder("onChanged")
            .addAnnotation(Override::class.java)
            .addModifiers(Modifier.PUBLIC)
        methodBuilder.addParameter(typeName, "t")
        // 如果typeName为NO_OBSERVER_PARAMS，则说明被@BusObserver注解的方法没有参数。
        // ((MainViewModel) host).method(s);
        val callbackStatement =
            "((${
                ClassName.get(hostClass).simpleName()
            }) host).${methodInfo.methodName}(${if (typeName == ClassGenerator.NO_ARGS) "" else "t"});"
        methodBuilder.addStatement(
            // 当参数为NO_OBSERVER_PARAMS时，代表被@BusObserver注解的方法没有参数。
            if (typeName == ClassGenerator.NO_ARGS) {
                // 为了和其它参数（可为null）区分开，需要判断null
                "if (t != null) {$callbackStatement}"
            } else {
                callbackStatement
            }
        )
        // 创建匿名内部类
        return TypeSpec.anonymousClassBuilder("")
            .addSuperinterface(ParameterizedTypeName.get(OBSERVER, typeName))
            .addMethod(methodBuilder.build())
            .build()
    }

}
