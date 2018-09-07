package com.nier.packer

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Action
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
open class PackerPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.extensions.create("packer", PackerExtension::class.java)
        project.afterEvaluate afterEvaluate@{
            //检查渠道信息配置，没有渠道信息就没有必要进行后续的多渠道打包了
            val customExtension = this.extensions["packer"] as? PackerExtension
                    ?: return@afterEvaluate
            println(">>> apkOutputDir = ${customExtension.apkOutputDir}")
            println(">>> apkNamePattern = ${customExtension.apkNamePattern}")
            println(">>> mappingOutputDir = ${customExtension.mappingOutputDir}")
            println(">>> channelContainer = ${customExtension.channelContainer}")
            //没有android组建说明配置有问题，不是android项目则不进行后续多渠道打包
            val appExtension = this.extensions["android"] as? AppExtension
                    ?: return@afterEvaluate

            //生成关联assemble task的渠道信息修改task，顺便生成group和root等依赖任务，方便分组或全部执行
            appExtension.applicationVariants.forEach(Consumer { variant ->
                generateTaskAndBuildDepends(project, customExtension, variant)
            })

            project.task("packerClean", Delete::class) {
                delete(customExtension.getOutputDir(project))
            }
        }
    }
}

/**
 * 生成添加渠道信息的task和相应的依赖任务
 */
private fun generateTaskAndBuildDepends(project: Project, extension: PackerExtension, variant: BaseVariant) {
    //create packTask
    val packTask = project.task("$PACK_TASK_PREFIX${variant.name.capitalize()}", InjectTask::class) {
        println("create Sub task -> ${this.name}")
        this.dependsOn.add(variant.assemble)
        this.extension = extension
        this.sourceVariant = variant
    }
    //create buildTypeTask
    val buildTypeName = "$PACK_TASK_PREFIX${variant.buildType.name.capitalize()}"
    val typeTask = project.tasks.findByName(buildTypeName)
            ?: generateTypeTask(project, buildTypeName)
    //buildTypeTask dependsOn packTask
    typeTask.dependsOn.add(packTask)

    //create rootTask
    val rootTask = project.tasks.findByName(ROOT_TASK_NAME)
            ?: generateRootTask(project)
    //rootTask dependsOn buildTypeTask, run rootTask build all apk
    if (typeTask !in rootTask.dependsOn) {
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
