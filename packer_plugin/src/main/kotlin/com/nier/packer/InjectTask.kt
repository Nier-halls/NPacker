package com.nier.packer

//import com.nier.inject.Apk
//import com.nier.inject.DEFAULT_EXTRA_PAYLOAD_KEY
//import com.nier.inject.IExtraPayload
import com.android.build.gradle.api.BaseVariant
import com.nier.packer.channel.Channel
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.IOException

/**
 * Created by Nier
 * Date 2018/7/26
 */
open class InjectTask : DefaultTask() {

    lateinit var sourceVariant: BaseVariant

    lateinit var apkName: String

    lateinit var outputDir: String

    lateinit var channel: Channel


    @TaskAction
    fun inject() {
        println("source variant = ${sourceVariant.name}, " +
                "real class = ${sourceVariant::class.java}")
        println("source variant output => ${sourceVariant.outputs
                ?.elementAt(0)
                ?.outputFile?.canonicalPath}")

        println("start Inject task.")
        val sourceApk = sourceVariant.outputs
                ?.elementAt(0)
                ?.outputFile
        val outputDirFile = File(outputDir)
        if (!outputDirFile.exists()) {
            outputDirFile.mkdirs()
        }
        val copiedApk = File(outputDir, apkName)
        if (verifyApk(sourceApk!!)) {
            println("Output dir >>> ${copiedApk.path}")
            copyFile(sourceApk, copiedApk)
            println("pre verify >>> ${verifyApk(copiedApk)}")
            NPacker.init(copiedApk).injectData(channel)
        } else {
            throw IOException("Invalid source apk (${sourceApk.path})")
        }
    }
}

//internal fun getOutputDir(packerExtension: PackerExtension?, project: Project): String? {
//    return packerExtension
//            ?.apkOutputDir
//            ?: "${project.rootDir}${File.separator}packer_output"
//}