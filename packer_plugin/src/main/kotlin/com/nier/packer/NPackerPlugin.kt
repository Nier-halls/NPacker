package com.nier.packer

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.BaseVariant
import com.nier.packer.channel.Channel
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Delete
import org.gradle.kotlin.dsl.*
import java.io.File

/**
 * Created by Nier
 * Date 2018/7/26
 *
 * todo 统一日志打印
 */
open class NPackerPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.extensions.create("npacker", PackerExtension::class.java)
        project.afterEvaluate afterEvaluate@{
            //检查渠道信息配置，没有渠道信息就没有必要进行后续的多渠道打包了
            val customExtension = this.extensions["npacker"] as? PackerExtension
                    ?: return@afterEvaluate
            //没有android组建说明配置有问题，不是android项目则不进行后续多渠道打包
            val appExtension = this.extensions["android"] as? AppExtension
                    ?: return@afterEvaluate
            //生成关联assemble task的渠道信息修改task，顺便生成group和root等依赖任务，方便分组或全部执行
            appExtension.applicationVariants.forEach { variant ->
                generateTaskAndBuildDepends(project, customExtension, variant)
            }
            //配置clean task
            customExtension.getOutputDir(project)?.apply {
                configCleanTask(project, this)
            }
        }
    }

    /**
     * 生成添加渠道信息的task和相应的依赖任务
     */
    private fun generateTaskAndBuildDepends(project: Project, extension: PackerExtension, variant: BaseVariant) {
        val channels = extension.channelContainer
        if (channels.isEmpty()) return

        //创建flavor task 依赖多渠道tasks
        val flavorTaskName = "$PACK_TASK_PREFIX${variant.name.capitalize()}"
        val flavorTask = project.tasks.findByName(flavorTaskName)
                ?: project.tasks.create(flavorTaskName)

        //创建所有渠道的tasks
        channels.forEach { (channelName, channel): Map.Entry<String, Channel> ->
            val channelTask = project.tasks.create("$PACK_TASK_PREFIX${channelName.capitalize()}${variant.name.capitalize()}", InjectTask::class) {
                this.dependsOn.add(variant.assembleProvider)
                this.apkName = extension.buildApkName(channelName, variant, project)
                this.outputDir = buildApkOutputFileDir(extension.getOutputDir(project), variant.flavorName, variant.buildType.name, channelName)
                this.channel = channel
                this.sourceVariant = variant
            }

            //单flavor task 依赖该flavor下的所有channel task
            if (channelTask !in flavorTask.dependsOn && flavorTask.name != channelTask.name) {
                flavorTask.dependsOn.add(channelTask)
            }
        }

        //创建buildType task
        val buildTypeTaskName = "$PACK_TASK_PREFIX${variant.buildType.name.capitalize()}"
        val typeTask = project.tasks.findByName(buildTypeTaskName)
                ?: project.tasks.create(buildTypeTaskName)
        //buildType Task 依赖该buildType对应的所有flavor tasks
        if (flavorTask !in typeTask.dependsOn && typeTask.name != flavorTask.name) {
            typeTask.dependsOn.add(flavorTask)
        }


        //创建多渠道打包总root task
        val rootTask = project.tasks.findByName(ROOT_TASK_NAME)
                ?: project.tasks.create(ROOT_TASK_NAME)
        //rootTask 依赖所有buildType tasks(间接依赖所有多渠道打包tasks)
        if (typeTask !in rootTask.dependsOn && rootTask.name != typeTask.name) {
            rootTask.dependsOn.add(typeTask)
        }
    }

    private fun buildApkOutputFileDir(packerOutput: String, flavor: String?, buildType: String?, channel: String): String {
        val packerOutputBlock = if (packerOutput[packerOutput.length - 1].toString() == File.separator) {
            packerOutput
        } else {
            packerOutput + File.separator
        }


        val flavorBlock = if (!flavor.isNullOrBlank()) {
            "${File.separator}$flavor"
        } else {
            ""
        }

        val buildTypeBlock = if (!buildType.isNullOrBlank()) {
            "${File.separator}$buildType"
        } else {
            ""
        }

        return packerOutputBlock + File.separator + channel + flavorBlock + buildTypeBlock
    }

    /**
     * 配置用于清除打包APK的clean task
     */
    private fun configCleanTask(project: Project, cleanDir: String) {
        val cleanTask = project.tasks.findByName("clean")
                ?: project.tasks.create("clean", mutableMapOf("type" to Delete::class.java))
        cleanTask.doLast {
            (this as Delete).delete(cleanDir)
        }
    }
}