import groovy.util.ConfigSlurper
import java.io.File


plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
}

//val kotlin_version = rootProject.extensions.extraProperties["kotlin_version"] as String
//val gradle_version = rootProject.extensions.extraProperties["gradle_version"] as String

buildscript {

    repositories {
        google()
        jcenter()

    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.31")
//        classpath("org.jetbrains.kotlin:kotlin-android-extensions:1.3.31")
    }
}

repositories {
    google()
    jcenter()
    maven {
        url = uri("./../publish")
    }
}

//val versionConfig = ConfigSlurper().parse(File("./../version.properties").toURI().toURL())
//
//extra.apply {
//    set("kotlin_version", versionConfig["kotlin"])
//    set("anko_version", versionConfig["anko"])
//    set("gradle_version", versionConfig["android_gradle_plugin"])
//}

group = "com.nier.packer"
version = "1.0"
//apply plugin: "org.gradle.kotlin.kotlin-dsl"


///**
// * 设置sourceSets
// */
//sourceSets {
//    main.java.srcDirs += 'src/main/kotlin'
//}




//
gradlePlugin {
    plugins {
        register("packer") {
            id = "packer"
            implementationClass = "com.nier.packer.PackerPlugin"
        }
    }
}
//
//
dependencies {
    implementation("com.android.tools.build:gradle:3.4.1")
    implementation("com.android.tools.build:apksig:3.4.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.3.31")
    implementation("com.nier.packer:helper:1.0")
    implementation(localGroovy())
    implementation(gradleApi())
}

publishing {
    repositories {
        maven(url = "../publish")
    }
}

repositories {
    mavenCentral()
}