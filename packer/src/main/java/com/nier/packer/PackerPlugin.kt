package com.nier.packer

import com.android.build.gradle.AppExtension
import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.kotlin.dsl.closureOf
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.task
import java.util.function.Consumer

/**
 * Created by Nier
 * Date 2018/7/26
 */
open class PackerPlugin : Plugin<Project> {

    override fun apply(project: Project?) {
        project ?: return
        project.extensions.create("packer", PackerExtension::class.java)
        project.afterEvaluate(Action {
            //extensions
            val customExtension = this.extensions["packer"] as? PackerExtension
            println("id = ${customExtension?.id}")
            println("map = ${customExtension?.map}")
            println("param = ${customExtension?.param}")

            val appExtension = this.extensions["android"] as? AppExtension ?: return@Action
            appExtension.applicationVariants.forEach(Consumer { variant ->
                //                project.task("packer${variant.name.capitalize()}" , type = InjectTask::class)
                project.task("packer${variant.name.capitalize()}", InjectTask::class) {
                    println("task name >>> ${this.name}")
                    this.dependsOn.add(variant.assemble)
                    this.packer = customExtension
                    this.sourceVariant = variant
                }
                println("variant -> ${variant.name}")
            })
        })
    }
}