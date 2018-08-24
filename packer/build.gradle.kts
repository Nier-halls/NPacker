import org.gradle.api.internal.HasConvention
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.load.kotlin.JavaFlexibleTypeDeserializer.id


plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `groovy`
    `java-library`
    `maven-publish`
}

group = "com.nier.packer"
version = "1.0"

val kotlin_version = rootProject.extensions.extraProperties["kotlin_version"] as String

gradlePlugin {
    (plugins) {
        "packer" {
            id = "packer"
            implementationClass = "com.nier.packer.PackerPlugin"
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
    jcenter()
    google()
}


//val sourceSets = java.sourceSets
//fun sourceSets(block: SourceSetContainer.() -> Unit) = sourceSets.apply(block)
//
//val SourceSetContainer.main: SourceSet get() = getByName("main")
////val SourceSetContainer.test: SourceSet get() = getByName("test")
//fun SourceSetContainer.main(block: SourceSet.() -> Unit) = main.apply(block)
////fun SourceSetContainer.test(block: SourceSet.() -> Unit) = test.apply(block)
//
//val SourceSet.kotlin: SourceDirectorySet
//    get() = (this as HasConvention).convention.getPlugin<KotlinSourceSet>().kotlin
//val SourceSet.groovy: SourceDirectorySet
//    get() = (this as HasConvention).convention.getPlugin<GroovySourceSet>().groovy
//var SourceDirectorySet.sourceDirs: Iterable<File>
//    get() = srcDirs
//    set(value) { setSrcDirs(value) }
//
//sourceSets {
//    main {
//        java.sourceDirs = files("src/main/java")
//        groovy.sourceDirs = files("src/main/groovy")
//        kotlin.sourceDirs = files("src/main/java")
//    }
//}
/**
 * 设置sourceSets
 */
val projectConvention: HasConvention = java.sourceSets
        .getByName("main") as HasConvention

val projectConvention1: HasConvention = project.convention
        .getPluginByName<JavaPluginConvention>("java")
        .sourceSets
        .getByName("main") as HasConvention


//println("java = ${java::class.java}")
//println("java.sourceSets = ${java.sourceSets::class.java}")
//println("projectConvention = ${projectConvention::class.java}")
//
//projectConvention.convention.plugins.forEach{(key,value) ->
//    println("convention key = $value")
//}
//
//task("printConvention") {
//    project.convention.plugins.forEach {
//        println("${it.key} -> ${it.value}")
//    }
//}


projectConvention.convention
        .getPlugin<GroovySourceSet>()
        .groovy
        .setSrcDirs(files("src/main/groovy"))

projectConvention.convention
        .getPlugin<KotlinSourceSet>()
        .kotlin
        .setSrcDirs(files("src/main/java", "src/main/kotlin"))

java.sourceSets
        .getByName("main")
        .java
        .setSrcDirs(files("src/main/java"))


dependencies {
    implementation ("com.android.tools.build:gradle:3.1.3")
    implementation ("com.android.tools.build:apksig:3.1.3")
//    compile "org.jetbrains.kotlin:kotlin-native-gradle-plugin:0.6.2"
    //    kotlin 标准库
//    compile "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version"
//    kotlin 反射库
//    compile "org.jetbrains.kotlin:kotlin-reflect:$kotlin_version"
//    implementation(kotlin("stdlib", "1.2.31"))
    api (project(":injector"))
    compile(localGroovy())
    compile(gradleApi())
    implementation (kotlin("stdlib", kotlin_version))
}

publishing {
    repositories {
        maven(url = uri("../publish"))
    }
}