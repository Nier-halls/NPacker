package com.nier.packer

import com.android.build.gradle.api.BaseVariant
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File

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
        println("start Inject task.")
        val sourceApk = sourceVariant?.outputs
                ?.elementAt(0)
                ?.outputFile
        println("output path >>> ${outputPath}")
        println("build dir >>> ${project.rootProject.buildDir}")
        val copiedApk = File(outputPath, "tmp${sourceVariant?.name?.capitalize()}.apk")
        if (verifyApk(sourceApk!!)) {
            println("verify success!!!")
            copyFile(sourceApk, copiedApk)
        } else {
            println("verify failed!!!")
        }

    }

}