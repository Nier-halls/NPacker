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
class InjectTask : DefaultTask() {

    @Input
    lateinit var packer: PackerExtensions

    @Input
    lateinit var sourceVariant: BaseVariant

    @TaskAction
    fun inject() {
        val sourceApk = sourceVariant.outputs
                ?.elementAt(0)
                ?.outputFile
        val copiedApk = File(project.rootProject.buildDir, "apk")
        if (verfyApk(sourceApk!!)) {
            copyFile(sourceApk, copiedApk)
        }
    }

}