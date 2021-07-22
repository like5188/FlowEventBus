package com.like.floweventbus_compiler

import com.like.floweventbus_annotations.BusObserver
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

/**
 * RxBus 注解处理器。每一个注解处理器类都必须有一个空的构造函数，默认不写就行;
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.like.floweventbus_annotations.BusObserver")
class BusProcessor : AbstractProcessor() {
    private val map = mutableMapOf<TypeElement, ClassGenerator>()

    /**
     * init()方法会被注解处理工具调用，并输入 ProcessingEnvironment 参数。
     * ProcessingEnvironment 提供很多有用的工具类 Elements, Types 和 Filer
     *
     * @param processingEnv 提供给 processor 用来访问工具框架的环境
     */
    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        ProcessUtils.mTypes = processingEnv.typeUtils
        ProcessUtils.mElements = processingEnv.elementUtils
        ProcessUtils.mFiler = processingEnv.filer
        ProcessUtils.mMessager = processingEnv.messager
    }

    /**
     * 这相当于每个处理器的主函数 main()，你在这里写你的扫描、评估和处理注解的代码，以及生成Java文件。
     * 输入参数 RoundEnvironment，可以让你查询出包含特定注解的被注解元素
     *
     * @param annotations 请求处理的注解类型
     * @param roundEnv    有关当前和以前的信息环境
     * @return 如果返回 true，则这些注解已声明并且不要求后续 BusProcessor 处理它们；
     * 如果返回 false，则这些注解未声明并且可能要求后续 BusProcessor 处理它们
     */
    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        // 返回使用给定注解类型的元素
        val methods = roundEnv.getElementsAnnotatedWith(BusObserver::class.java) as Set<Element>
        if (methods.isEmpty()) {
            return false
        }
        for (method in methods) {
            try {
                // 验证方法及其所在宿主类的有效性
                if (!ProcessUtils.verifyEnclosingClass(method) || !ProcessUtils.verifyMethod(method))
                    continue
                // 添加宿主类
                val hostClass = method.enclosingElement as TypeElement
                val classGenerator = map[hostClass] ?: ClassGenerator().apply {
                    map[hostClass] = this
                }
                // 添加被BusObserver注解的方法
                classGenerator.addMethod(method)
            } catch (e: Exception) {
                e.printStackTrace()
                ProcessUtils.error(method, e.message ?: "")
            }
        }

        // 生成宿主类对应的代理类的代码
        map.forEach { (_, classGenerator) ->
            classGenerator.create()
        }
        return true
    }

}