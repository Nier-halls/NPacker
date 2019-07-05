import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

plugins {
    kotlin("jvm") version "1.2.60"
    `kotlin-dsl`
    `java-library`
    `java-gradle-plugin`
}

group = "com.nier.packer"
version = "1.0"

gradlePlugin {
    (plugins) {
        "packer" {
            id = "packer"
            implementationClass = "com.nier.npacker.TestGroovyPlugin"
        }
    }
}

buildscript {
    repositories {
        mavenCentral()
        jcenter()
        google()
    }
}

repositories {
    mavenCentral()
}




sourceSets.main {
    withConvention(KotlinSourceSet::class) {
        kotlin.srcDirs("src/main/kotlin", "src/main/java")
    }
    withConvention(GroovySourceSet::class) {
        groovy.srcDirs("src/main/groovy", "src/main/java")
    }
}

dependencies {
    implementation("com.android.tools.build:gradle:3.4.1")
    implementation("com.android.tools.build:apksig:3.4.1")
//    implementation("com.nier.packer:helper:1.0")
    implementation(localGroovy())
    implementation(gradleApi())
    implementation(kotlin("stdlib"))
}