package com.like.floweventbus_compiler

import com.like.floweventbus_annotations.BusObserver
import com.squareup.javapoet.*
import java.io.IOException
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
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
class ClassCodeGenerator {
    companion object {
        private const val CLASS_UNIFORM_MARK = "_Proxy"

        // 因为java工程中没有下面这些类(Android中的类)，所以只能采用ClassName的方式。
        private val HOST_PROXY = ClassName.get("com.like.floweventbus", "HostProxy")
        private val OBSERVER = ClassName.get("androidx.lifecycle", "Observer")
        private val LIFECYCLE_OWNER = ClassName.get("androidx.lifecycle", "LifecycleOwner")
        private val CLASS = ClassName.get("java.lang", "Class")
        private val STRING = ClassName.get("java.lang", "String")
        private val NO_ARGS = ClassName.get("com.like.floweventbus", "NoArgs")
        private val EVENT_MANAGER = ClassName.get("com.like.floweventbus", "EventManager")
    }

    private var mHostClass: TypeElement? = null// 宿主类，通过它的一些信息来创建代理类。
    private val mMethodInfoList = mutableSetOf<MethodInfo>()// 类中的所有方法

    fun create() {
        if (mHostClass == null || mMethodInfoList.isEmpty()) {
            return
        }
        // 创建包名及类的注释
        val javaFile = JavaFile.builder(ClassName.get(mHostClass).packageName(), createClass())
            .addFileComment(" This codes are generated automatically by FlowEventBus. Do not modify!")// 类的注释
            .build()

        try {
            javaFile.writeTo(ProcessUtils.mFiler)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    /**
     * 创建类
     *
     * public class MainViewModel_Proxy<T extends User> extends HostProxy {}
     */
    private fun createClass(): TypeSpec {
        val builder = TypeSpec.classBuilder(ClassName.get(mHostClass).simpleName() + CLASS_UNIFORM_MARK)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .superclass(HOST_PROXY)
            .addMethod(createSubscribeEventMethod())
            .addMethod(createRegisterHostMethod())
        // 如果宿主类有泛型，也需要添加到代理类中。
        mHostClass?.typeParameters?.forEach {
            builder.addTypeVariable(TypeVariableName.get(it))
        }
        return builder.build()
    }

    /**
     * 创建 subscribeEvent 方法
     *
     * @Override
     * public void subscribeEvent(final Class hostClass, final String tag, final String requestCode, final boolean isSticky) {}
     */
    private fun createSubscribeEventMethod(): MethodSpec {
        val builder = MethodSpec.methodBuilder("subscribeEvent")
            .addModifiers(Modifier.PUBLIC)
            .addParameter(CLASS, "hostClass", Modifier.FINAL)
            .addParameter(STRING, "tag", Modifier.FINAL)
            .addParameter(STRING, "requestCode", Modifier.FINAL)
            .addParameter(TypeName.BOOLEAN, "isSticky", Modifier.FINAL)
            .addAnnotation(Override::class.java)
        for (binder in mMethodInfoList) {
            builder.addCode(createSubscribeEventMethodCodeBlock(binder))
        }
        return builder.build()
    }

    /**
     * 创建 registerHost 方法中调用的方法
     *
     * com.like.floweventbus.EventManager.subscribeEvent(hostClass, tag, requestCode, isSticky)
     */
    private fun createSubscribeEventMethodCodeBlock(methodInfo: MethodInfo): CodeBlock {
        val builder = CodeBlock.builder()
        methodInfo.tags?.forEach {
            val requestCode = methodInfo.requestCode
            val isSticky = methodInfo.isSticky

            val codeBlockBuilder = CodeBlock.builder()
            codeBlockBuilder.addStatement(
                "\$L.subscribeEvent(\nhostClass\n,\$S\n,\$S\n,\$L)",
                EVENT_MANAGER,
                it,
                requestCode,
                isSticky
            )
            builder.add(codeBlockBuilder.build())
        }
        return builder.build()
    }

    /**
     * 创建 registerHost 方法
     *
     * @Override
     * public void registerHost(final Object host, final LifecycleOwner owner) {}
     */
    private fun createRegisterHostMethod(): MethodSpec {
        val builder = MethodSpec.methodBuilder("registerHost")
            .addModifiers(Modifier.PUBLIC)
            .addParameter(TypeName.OBJECT, "host", Modifier.FINAL)
            .addParameter(LIFECYCLE_OWNER, "owner", Modifier.FINAL)
            .addAnnotation(Override::class.java)
        for (binder in mMethodInfoList) {
            builder.addCode(createRegisterHostMethodCodeBlock(binder))
        }
        return builder.build()
    }

    /**
     * 创建 registerHost 方法中调用的方法
     *
     * com.like.floweventbus.EventManager.register(host, owner, observer)
     */
    private fun createRegisterHostMethodCodeBlock(methodInfo: MethodInfo): CodeBlock {
        val builder = CodeBlock.builder()

        val codeBlockBuilder = CodeBlock.builder()
        codeBlockBuilder.addStatement(
            "\$L.registerHost(\nhost\n,owner\n,\$L)",
            EVENT_MANAGER,
            createObserverParam(methodInfo)
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
    private fun createObserverParam(methodInfo: MethodInfo): TypeSpec {
        // 获取onChanged方法的参数类型
        var typeName: TypeName = NO_ARGS
        methodInfo.paramType?.let {
            if (it.kind.isPrimitive) {
                typeName = TypeName.get(it)
                if (!typeName.isBoxedPrimitive)// 如果是装箱数据类型
                    typeName = typeName.box()
            } else
                typeName = ClassName.get(it)
        }

        // 创建onChanged方法
        val methodBuilder = MethodSpec.methodBuilder("onChanged")
            .addAnnotation(Override::class.java)
            .addModifiers(Modifier.PUBLIC)
        methodBuilder.addParameter(typeName, "t")
        // 如果typeName为NO_OBSERVER_PARAMS，则说明被@BusObserver注解的方法没有参数。
        // ((MainViewModel) host).method(s);
        val callbackStatement =
            "((${
                ClassName.get(mHostClass).simpleName()
            }) host).${methodInfo.methodName}(${if (typeName == NO_ARGS) "" else "t"});"
        methodBuilder.addStatement(
            // 当参数为NO_OBSERVER_PARAMS时，代表被@BusObserver注解的方法没有参数。
            if (typeName == NO_ARGS) {
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

    /**
     * 添加元素，用于生成类
     */
    fun addElement(element: Element) {
        if (mHostClass == null) {
            mHostClass = element.enclosingElement as? TypeElement
        }
        mHostClass ?: return

        val busObserverAnnotationClass = BusObserver::class.java
        val methodInfo = MethodInfo()
        methodInfo.tags = element.getAnnotation(busObserverAnnotationClass).value
        if (methodInfo.tags.isNullOrEmpty()) return

        methodInfo.requestCode = element.getAnnotation(busObserverAnnotationClass).requestCode

        // 判断是否有重复的tag + requestCode
        val isRepeat = mMethodInfoList.any {
            it.tags?.intersect(methodInfo.tags!!.toList())?.isNotEmpty() ?: false &&
                    it.requestCode == methodInfo.requestCode
        }
        if (isRepeat) return

        methodInfo.methodName = element.simpleName.toString()
        if (methodInfo.methodName.isEmpty()) return

        methodInfo.isSticky = element.getAnnotation(busObserverAnnotationClass).isSticky

        val executableElement = element as ExecutableElement
        if (executableElement.parameters.size == 1) {
            val ve = executableElement.parameters[0]
            methodInfo.paramType = ve.asType()
        }
        mMethodInfoList.add(methodInfo)
    }
}