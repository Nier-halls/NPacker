package com.nier.packer

import com.android.build.gradle.api.BaseVariant
//import com.nier.inject.Apk
//import com.nier.inject.DEFAULT_EXTRA_PAYLOAD_KEY
//import com.nier.inject.IExtraPayload
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.IOException
import java.nio.charset.Charset

/**
 * Created by Nier
 * Date 2018/7/26
 */
open class InjectTask : DefaultTask() {
    val outputPath = "${project.rootDir}${File.separator}injector"

    var packerExtension: PackerExtension? = null

    var sourceVariant: BaseVariant? = null

    @TaskAction
    fun inject() {
        if (sourceVariant == null) {
            println("invalid variant, variant is null")
            return
        }

        println("start Inject task.")
        val sourceApk = sourceVariant?.outputs
                ?.elementAt(0)
                ?.outputFile
        println("output path >>> $outputPath")
        println("build dir >>> ${project.rootProject.buildDir}")
        val copiedApk = File(outputPath, "tmp${sourceVariant?.name?.capitalize()}.apk")
        if (verifyApk(sourceApk!!)) {
            copyFile(sourceApk, copiedApk)
//            Apk.createApk(copiedApk).injectExtraData(object : IExtraPayload {
//                override fun key(): Int {
//                    return DEFAULT_EXTRA_PAYLOAD_KEY
//                }
//
//                override fun flat(): ByteArray {
//                    return sourceVariant?.name?.toByteArray(Charsets.UTF_8)
//                            ?: throw IllegalArgumentException("Unknow extra payload data.")
//                }
//            })
        } else {
            throw IOException("Invalid source apk (${sourceApk.path})")
        }

    }

}