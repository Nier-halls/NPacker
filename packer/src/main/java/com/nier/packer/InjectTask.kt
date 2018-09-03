package com.nier.packer

//import com.nier.inject.Apk
//import com.nier.inject.DEFAULT_EXTRA_PAYLOAD_KEY
//import com.nier.inject.IExtraPayload
import com.android.build.gradle.api.BaseVariant
import com.nier.inject.Apk
import com.nier.inject.DEFAULT_EXTRA_PAYLOAD_KEY
import com.nier.inject.IExtraPayload
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Nier
 * Date 2018/7/26
 */
open class InjectTask : DefaultTask() {
//    val outputPath = "${project.rootDir}${File.separator}injector"

    var packerExtension: PackerExtension? = null

    lateinit var sourceVariant: BaseVariant

    @TaskAction
    fun inject() {
//        if (sourceVariant == null) {
//            println("invalid variant, variant is null")
//            return
//        }
        val channels = packerExtension?.channelContainer?.container ?: return
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
            println("output path >>> ${getOutputDir(it.key)}")
            println("build dir >>> ${project.rootProject.buildDir}")
            val copiedApk = File(getOutputDir(it.key), "tmp${sourceVariant.name?.capitalize()}.apk")
            if (verifyApk(sourceApk!!)) {
                copyFile(sourceApk, copiedApk)
                println("pre verify >>> ${verifyApk(copiedApk)}")
                Apk.createApk(copiedApk).injectExtraData(object : IExtraPayload {
                    override fun key(): Int {
                        return DEFAULT_EXTRA_PAYLOAD_KEY
                    }

                    override fun flat(): ByteArray {
                        return "new version fgd hahahahahaha !@#$%^&*()_+${sourceVariant.name}>>>${sourceVariant}".toByteArray(Charsets.UTF_8)
                                ?: throw IllegalArgumentException("Unknow extra payload data.")
                    }
                })
            } else {
                throw IOException("Invalid source apk (${sourceApk.path})")
            }
        }
    }

    fun getOutputDir(channel: String): String? {
        return packerExtension
                ?.apkOutputDir
                ?: "${project.rootDir}${File.separator}packer"
    }


    private fun templateMap(channel: String): HashMap<String, Any?> {
        return hashMapOf<String, Any?>(
                "appName" to project.name,
                "projectName" to project.rootProject.name,
                "channel" to channel,
                "flavor" to sourceVariant.flavorName,
                "buildType" to sourceVariant.buildType.name,
//                "versionName" to sourceVariant.,
//                "versionCode" to sourceVariant.versionCode,
                "appPkg" to sourceVariant.applicationId,
                "buildTime" to SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        )
    }


}