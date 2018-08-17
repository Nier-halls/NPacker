package com.nier.packer

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.BaseVariant
import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.internal.HasConvention
import org.gradle.api.plugins.Convention
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.kotlin.dsl.*
import java.util.function.Consumer

/**
 * Created by Nier
 * Date 2018/7/26
 */
open class PackerPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.extensions.create("packer", PackerExtension::class.java)
        project.afterEvaluate(Action {
            //extensions
            val customExtension = this.extensions["packer"] as? PackerExtension
            println("id = ${customExtension?.id}")
            println("map = ${customExtension?.map}")
            println("param = ${customExtension?.param}")

            val appExtension = this.extensions["android"] as? AppExtension ?: return@Action
            appExtension.applicationVariants.forEach(Consumer { variant ->
                generateTaskAndBuildDepends(project, variant)
            })
        })
    }
}


fun generateTaskAndBuildDepends(project: Project, variant: BaseVariant) {
    val customExtension = project.extensions["packer"] as? PackerExtension
    //create packTask
    val packTask = project.task("${PACK_TASK_PREFIX}${variant.name.capitalize()}", InjectTask::class) {
        println("create task -> ${this.name}")
        this.dependsOn.add(variant.assemble)
        this.packerExtension = customExtension
        this.sourceVariant = variant
    }
    //create buildTypeTask
    val buildTypeName = "${PACK_TASK_PREFIX}${variant.buildType.name.capitalize()}"
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

val generateRootTask: (Project) -> Task = {
    val rootTask = it.task(ROOT_TASK_NAME) {
        //todo print and check configuration
        println("create root task -> ${this.name}")
    }
    rootTask
}

val generateTypeTask: (Project, String) -> Task = { project, name ->
    project.task(name) {
        println("create type task -> ${this.name}")
    }
}
