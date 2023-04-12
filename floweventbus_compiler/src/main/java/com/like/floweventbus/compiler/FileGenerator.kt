package com.like.floweventbus.compiler

import java.io.IOException
import javax.tools.StandardLocation

/*
在 /resources/META-INF/services 目录下创建以实现的接口的全名(包名+类名)为名字的文本文件, 里面是该接口的实现类全名.
最终生成的目录结构为：
resources
    META-INF/services
        com.like.floweventbus.Initializer
 */
/**
 * 创建文件
 */
object FileGenerator {

    fun generateConfigFiles(fileName: String, content: String) {
        val filer = ProcessUtils.mFiler ?: return
        val resourceFile = "META-INF/services/$fileName"
        try {
            val fileObject = filer.createResource(
                StandardLocation.CLASS_OUTPUT, "",
                resourceFile
            )
            fileObject.openOutputStream().bufferedWriter().use { writer ->
                writer.write(content)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}