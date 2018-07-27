package com.nier.packer

import com.android.build.gradle.api.BaseVariant
import groovy.lang.Closure
import org.codehaus.groovy.runtime.DefaultGroovyMethods
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by Nier
 * Date 2018/7/26
 */
public open class PackerPlugin : Plugin<Project> {

    public override fun apply(project: Project?) {
        project ?: return

        project.afterEvaluate(Action (})



//        {
//            PluginUtils.iterateVariant(this) {
//
//
//            }
//        }
    }
}