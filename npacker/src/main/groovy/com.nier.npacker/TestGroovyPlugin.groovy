package com.nier.npacker

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by fgd
 * Date 2019/6/3 16:29
 */
public class TestGroovyPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println("hello world test groovy.")
    }
}
