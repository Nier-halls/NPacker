package com.nier.packer

//import com.nier.inject.Apk
//import com.nier.inject.DEFAULT_EXTRA_PAYLOAD_KEY
//import com.nier.inject.IExtraPayload
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.IOException

/**
 * Created by Nier
 * Date 2018/7/26
 */
open class InjectTask : DefaultTask() {
//    val outputPath = "${project.rootDir}${File.separator}injector"

    lateinit var extension: PackerExtension

    lateinit var sourceVariant: BaseVariant

    @TaskAction
    fun inject() {
        if (extension == null) {
            println("invalid variant, variant is null")
            return
        }
        val channels = extension.channelContainer
        if (channels.isEmpty()) return

        println("source variant = ${sourceVariant.name}, " +
                "real class = ${sourceVariant::class.java}")
        println("source variant output => ${sourceVariant.outputs
                ?.elementAt(0)
                ?.outputFile?.canonicalPath}")


        channels.forEach {
            println("start Inject task.")
            val sourceApk = sourceVariant.outputs
                    ?.elementAt(0)
                    ?.outputFile
            val outputDir = extension.getOutputDir(project)
            val outputDirFile = File(outputDir)
            if (!outputDirFile.exists()) {
                outputDirFile.mkdirs()
            }
            val copiedApk = File(extension.getOutputDir(project), extension.buildApkName(it.key, sourceVariant, project))
            if (verifyApk(sourceApk!!)) {
                println("Output dir >>> ${copiedApk.path}")
                copyFile(sourceApk, copiedApk)
                println("pre verify >>> ${verifyApk(copiedApk)}")
                Packer.init(copiedApk).injectData(it.value)
            } else {
                throw IOException("Invalid source apk (${sourceApk.path})")
            }
        }
    }
}

//internal fun getOutputDir(packerExtension: PackerExtension?, project: Project): String? {
//    return packerExtension
//            ?.apkOutputDir
//            ?: "${project.rootDir}${File.separator}packer_output"
//}