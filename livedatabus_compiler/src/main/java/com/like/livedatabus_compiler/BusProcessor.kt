package com.like.livedatabus_compiler

import com.like.livedatabus_annotations.BusObserver
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

/**
 * RxBus 注解处理器。每一个注解处理器类都必须有一个空的构造函数，默认不写就行;
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.like.livedatabus_annotations.BusObserver")
class BusProcessor : AbstractProcessor() {
    companion object {
        private val CODE_BUILDER_MAP = mutableMapOf<TypeElement, ClassCodeGenerator>()
    }

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
        val elements = roundEnv.getElementsAnnotatedWith(BusObserver::class.java) as Set<Element>
        if (elements.isEmpty()) {
            return false
        }
        for (element in elements) {
            try {
                // 验证方法及其所在宿主类的有效性
                if (!ProcessUtils.verifyEnclosingClass(element) || !ProcessUtils.verifyMethod(element))
                    continue
                // 添加宿主类
                val enclosingElement = element.enclosingElement as TypeElement// 获取直接上级
                var classCodeGenerator = CODE_BUILDER_MAP[enclosingElement]
                if (classCodeGenerator == null) {
                    classCodeGenerator = ClassCodeGenerator()
                    CODE_BUILDER_MAP[enclosingElement] = classCodeGenerator
                }
                // 添加被BusObserver注解的方法
                classCodeGenerator.addElement(element)
            } catch (e: Exception) {
                e.printStackTrace()
                ProcessUtils.error(element, e.message ?: "")
            }
        }

        // 生成宿主类对应的代理类的代码
        CODE_BUILDER_MAP.forEach { (_, classCodeGenerator) ->
            classCodeGenerator.create()
        }
        return true
    }

}