package com.like.floweventbus_compiler

import java.io.BufferedWriter
import java.io.IOException
import java.io.OutputStreamWriter
import javax.tools.StandardLocation

object FileGenerator {

    fun generateConfigFiles(fileName: String, data: String) {
        val filer = ProcessUtils.mFiler ?: return
        val resourceFile = "META-INF/services/$fileName"
        try {
            val fileObject = filer.createResource(
                StandardLocation.CLASS_OUTPUT, "",
                resourceFile
            )
            fileObject.openOutputStream().use { out ->
                val writer = BufferedWriter(OutputStreamWriter(out, Charsets.UTF_8))
                writer.write(data)
                writer.newLine()
                writer.flush()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}