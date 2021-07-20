package com.like.livedatabus_compiler

import com.like.livedatabus_annotations.BusObserver
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind.CLASS
import javax.lang.model.element.ElementKind.METHOD
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier.PUBLIC
import javax.lang.model.element.Modifier.STATIC
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic

/**
 * 用于验证目标类及方法的正确性
 */
object ProcessUtils {
    var mTypes: Types? = null// 用来处理TypeMirror的工具
    var mElements: Elements? = null// 用来处理Element的工具
    var mFiler: Filer? = null// 用来生成我们需要的.java文件的工具
    var mMessager: Messager? = null// 提供给注解处理器一个报告错误、警告以及提示信息的途径

    /**
     * 判断使用[BusObserver]注解的方法所在的类（宿主）的有效性
     * <p>
     * 1、宿主型必须为类。
     * 2、宿主必须被 public 修饰。
     * 3、包名不能以 android.、androidx.、java.、javax. 开头，因为是系统的类，不可能有BusObserver注解。
     *
     * @param element
     * @return
     */
    fun verifyEnclosingClass(element: Element): Boolean {
        val enclosingElement = element.enclosingElement as TypeElement
        if (enclosingElement.kind != CLASS) {
            error(element, "宿主：%s 不属于 CLASS 类型", element.simpleName.toString())
            return false
        }
        if (!enclosingElement.modifiers.contains(PUBLIC)) {
            error(element, "宿主：%s 必须被 public 修饰", element.simpleName.toString())
            return false
        }
        val qualifiedName = enclosingElement.qualifiedName.toString()
        if (qualifiedName.startsWith("android.") ||
            qualifiedName.startsWith("androidx.") ||
            qualifiedName.startsWith("java.") ||
            qualifiedName.startsWith("javax.")
        ) {
            error(element, "宿主：%s 的包名不能以`android.`、`androidx.`、`java.`、`javax.`开头", element.simpleName.toString())
            return false
        }
        return true
    }

    /**
     * 判断使用[BusObserver]注解的方法的有效性
     * <p>
     * 1、类型必须为 method。
     * 2、必须被 public 修饰。
     * 3、不能被 static 修饰。
     * 4、方法的参数最多只能是1个。因为 LiveData 只能传递一个数据。
     *
     * @param element
     * @return
     */
    fun verifyMethod(element: Element): Boolean {
        if (element.kind != METHOD) {
            error(element, "方法：%s 不属于 METHOD 类型", element.simpleName.toString())
            return false
        }

        if (!element.modifiers.contains(PUBLIC) || element.modifiers.contains(STATIC)) {
            error(element, "方法：%s 必须被 public 修饰，且不能为 static", element.simpleName.toString())
            return false
        }

        val executableElement = element as ExecutableElement
        if (executableElement.parameters.size > 1) {
            error(executableElement, "方法：%s 的参数最多只能有1个", executableElement.simpleName.toString())
            return false
        }

        return true
    }

    fun error(element: Element, format: String, vararg args: Any) {
        val f = if (args.isNotEmpty())
            String.format(format, *args)
        else
            format
        mMessager?.printMessage(Diagnostic.Kind.ERROR, f, element)
    }

}