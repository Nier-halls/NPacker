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

    var packer: PackerExtension? = null

    var sourceVariant: BaseVariant? = null

    @TaskAction
    fun inject() {
        println("start Inject task.")
        val sourceApk = sourceVariant?.outputs
                ?.elementAt(0)
                ?.outputFile
        println("sourceApk path >>> ${sourceApk?.absolutePath}")
        val copiedApk = File(project.rootProject.buildDir, "tmp.apk")
        if (verifyApk(sourceApk!!)) {
            println("verify success!!!")
            copyFile(sourceApk, copiedApk)
        }
    }

}