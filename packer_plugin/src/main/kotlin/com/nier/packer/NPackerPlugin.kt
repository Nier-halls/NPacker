package com.nier.packer

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.BaseVariant
import com.nier.packer.channel.Channel
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Delete
import org.gradle.kotlin.dsl.*
import java.util.function.Consumer

/**
 * Created by Nier
 * Date 2018/7/26
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

        //create single channelTask
        extension.channelContainer.forEach { channelEntry: Map.Entry<String, Channel> ->
            project.task("$PACK_TASK_PREFIX${channelEntry.key.capitalize()}${variant.name.capitalize()}", InjectTask::class) {
                this.dependsOn.add(variant.assembleProvider)
                this.extension = extension.clone()
                val singleChannelContainer = ChannelContainer()
                singleChannelContainer[channelEntry.key] = channelEntry.value
                this.extension.channelContainer = singleChannelContainer
                this.sourceVariant = variant
//            (variant.buildType as BaseConfigImpl).addBuildConfigField(ClassFieldImpl("String", "testString", "${channelEntry.key}2233"))
//            (variant .buildType.buildConfigFields as MutableMap<String, ClassField>)["testString"] =
//                    ClassFieldImpl("String", "testString", "${channelEntry.key}2233")
            }
        }

        //创建FlavorTask flavor 还有必要吗?
        val packTask = project.task("$PACK_TASK_PREFIX${variant.name.capitalize()}", InjectTask::class) {
            this.dependsOn.add(variant.assembleProvider)
            this.extension = extension
            this.sourceVariant = variant
        }

        //创建buildTypeTask
        val buildTypeName = "$PACK_TASK_PREFIX${variant.buildType.name.capitalize()}"
        val typeTask = project.tasks.findByName(buildTypeName)
                ?: generateTypeTask(project, buildTypeName)
        //buildTypeTask dependsOn packTask
        if (packTask !in typeTask.dependsOn && typeTask.name != packTask.name) {
            typeTask.dependsOn.add(packTask)
        }

        //create rootTask
        val rootTask = project.tasks.findByName(ROOT_TASK_NAME)
                ?: generateRootTask(project)
        //rootTask dependsOn buildTypeTask, run rootTask build all apk
        if (typeTask !in rootTask.dependsOn && rootTask.name != typeTask.name) {
            rootTask.dependsOn.add(typeTask)
        }
    }

    /**
     * 生成总任务，依赖所有子任务，方便直接执行所有渠道打包
     */
    private val generateRootTask: (Project) -> Task = {
        val rootTask = it.task(ROOT_TASK_NAME) {
            //todo print and check configuration
            println("create Root task -> ${this.name}")
        }
        rootTask
    }

    /**
     * 分组任务，分组依赖子任务，方便分组一大包部分渠道
     * 具体根据builtType的name来进行分组
     */
    private val generateTypeTask: (Project, String) -> Task = { project, name ->
        project.task(name) {
            println("create Type Group task -> ${this.name}")
        }
    }

    /**
     * 配置用于清除打包APK的clean task
     */
    private fun configCleanTask(project: Project, cleanDir: String) {
        project.tasks.named("clean").get().doLast(closureOf<Any> {
            (this as Delete).delete(cleanDir)
        })
    }
}