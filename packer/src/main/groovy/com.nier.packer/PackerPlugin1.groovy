package com.nier.packer

import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * plugin不能用kt写，task也不能用kt写!!!
 */

class PackerPlugin1 implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.afterEvaluate {
            it.android.applicationVariants.all { BaseVariant variant ->
                println("variant --> ${variant.name}")
//                project.task("pack${it.name.capitalize()}",
//                        type: InjectTask) {
//                    dependsOn variant.assemble
//                    packer = project.packer
//                    sourceVariant = variant
//                }
            }
        }
    }
}