package com.like.floweventbus.compiler

import com.like.floweventbus.annotations.BusObserver
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

/*
Element有以下几个实现类
PackageElement 			表示一个包程序元素。提供对有关包及其成员的信息的访问
ExecutableElement 		表示某个类或接口的方法、构造方法或初始化程序（静态或实例）
TypeElement 			表示一个类或接口程序元素。提供对有关类型及其成员的信息的访问
VariableElement 		表示一个字段、enum 常量、方法或构造方法参数、局部变量或异常参数

Element节点中的API
getEnclosedElements() 	返回该元素直接包含的子元素
getEnclosingElement() 	返回包含该element的父element，与上一个方法相反
getKind() 				返回element的类型，判断是哪种element
getModifiers() 			获取修饰关键字,入public static final等关键字
getSimpleName()			获取名字，不带包名
getQualifiedName() 		获取全名，如果是类的话，包含完整的包名路径
getParameters() 		获取方法的参数元素，每个元素是一个VariableElement
getReturnType() 		获取方法元素的返回值
getConstantValue() 		如果属性变量被final修饰，则可以使用该方法获取它的值

 */

/**
 * FlowEventBus 框架的注解处理器。每一个注解处理器类都必须有一个空的构造函数，默认不写就行;
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.like.floweventbus.annotations.BusObserver")
class FlowEventBusProcessor : AbstractProcessor() {

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
        val elements = roundEnv.getElementsAnnotatedWith(BusObserver::class.java)
        if (elements.isEmpty()) {
            return false
        }
        val classGenerator = Generator()
        for (element in elements) {
            try {
                // 验证方法的有效性
                if (!ProcessUtils.verifyMethod(element))
                    continue
                // 添加被BusObserver注解的方法
                classGenerator.addElement(element)
            } catch (e: Exception) {
                e.printStackTrace()
                ProcessUtils.error(element, e.message ?: "")
            }
        }

        // 生成宿主类对应的代理类及它的配置文件
        val buildConfigElement = roundEnv.rootElements.first { it.simpleName.toString() == "BuildConfig" }
        val packageName = ProcessUtils.mElements?.getPackageOf(buildConfigElement)
        classGenerator.generate(packageName.toString())
        return true
    }

}