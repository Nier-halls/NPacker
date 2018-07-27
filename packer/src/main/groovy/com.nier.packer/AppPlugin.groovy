package com.nier.packer

import com.android.build.gradle.api.BaseVariant
import com.android.builder.model.Variant
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction


class AppPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        project.extensions.create("packer", Extensions.class)
        def mTask1 = project.task("fakeDebugAssemble", type: MyTask)
        mTask1.doLast {
            println("${it.name} doLast has been invoked.")
        }

        def mTask2 = project.task("fakeReleaseAssemble", type: MyTask)
        mTask2.doLast {
            println("${it.name} doLast has been invoked.")
        }


        project.afterEvaluate {
            testApplicationVariants(project, mTask1, mTask2)
        }
    }

    void testApplicationVariants(Project project, MyTask task1, MyTask task2) {
        println("testApplicationVariants")
        project.android.applicationVariants.all { BaseVariant variant ->
            if (variant != null) {
                println("variant = ${variant.name}, class -> ${variant.getClass()}")
            } else {
                println("variant = NULL!")
            }

            // parent会被执行几次  MyTask又会被执行几次
            def mTestTask = project.task("packer${variant.name.capitalize()}") { internalTask ->
//                dependsOn variant.assemble
                println("custom task ${internalTask.name} configuration start!!!")
                if ("debug".equalsIgnoreCase(variant.buildType.name)) {
                    dependsOn task1
                } else {
                    dependsOn task2
                }
                println("custom task ${internalTask.name} configuration finish!!!")
            }
            mTestTask.doLast {
                def map = project.packer.map
                def param = project.packer.param
                def id = project.packer.id
                println("extensions->map = $map")
                println("extensions->param = $param")
                println("extensions->id = $id")
                println("project.findProperty->fgd = ${project.findProperty("fgd")}")
                println("project.findProperty->age = ${project.findProperty("age")}")
                println("custom task ${it.name} closure DONE!!!")
            }

            def buildTypeName = variant.buildType.name
            if (variant.name != null) {
                def taskName = "packer${buildTypeName.capitalize()}"
                def parentTask = project.tasks.findByName(taskName)
                if (parentTask == null) {
                    parentTask = project.task(taskName) {
                        println("custom PARENT task ${it.name} configuration invoke!!!")
                    }
                    parentTask.doLast {
                         println("custom PARENT task ${it.name} closure DONE!!!")
                    }
                }
                parentTask.dependsOn(mTestTask)

                def packer = project.tasks.findByName("packer")
                if (packer == null) {
                    packer = project.task("packer")
                    packer.doLast {
                        println("packer has bean invoked.")
                    }
                }
                packer.dependsOn(parentTask)
            }

        }
    }

    void testAddMyTask(Project project, Variant variant) {

    }
}

class MyTask extends DefaultTask {
    @TaskAction
    void generate() {
        println("TaskAction invoked!!!")
    }
}

class RealTask extends DefaultTask {
    @TaskAction
    void generate() {
        println("RealTask TaskAction invoked!!!")
    }
}