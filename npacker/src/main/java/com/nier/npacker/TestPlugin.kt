package com.nier.npacker

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by fgd
 * Date 2019/6/3 16:02
 */
open class TestPlugin : Plugin<Project> {
    override fun apply(p0: Project) {

        print("hello world TestPlugin.")
    }
}