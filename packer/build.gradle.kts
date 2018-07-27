import org.gradle.api.internal.HasConvention
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.load.kotlin.JavaFlexibleTypeDeserializer.id

//apply plugin: 'groovy'
//apply plugin: 'maven'
//plugins {
//    id "org.gradle.kotlin.kotlin-dsl" version "0.19.3"
//}

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `groovy`
    `maven-publish`
}
group = "com.nier.packer"
version = "1.0"
gradlePlugin {
    (plugins) {
        "packer" {
            id = "packer"
            implementationClass = "com.nier.packer.PackerPlugin"
        }
    }
}

buildscript {
//    ext.kotlin_version = '1.2.30'
    repositories {
        mavenCentral()
        jcenter()
        google()
//        maven { url 'https://dl.bintray.com/kotlin/kotlin-eap' }
//        maven {
//            url "https://plugins.gradle.org/m2/"
//        }
    }
//    dependencies {
//        classpath "gradle.plugin.org.gradle.kotlin:gradle-kotlin-dsl-plugins:0.19.3"
//    }
}

//plugins {
//    kotlin("jvm") version "1.2.31"
//}

repositories {
    mavenCentral()
    jcenter()
    google()
}

val sourceSets = java.sourceSets
fun sourceSets(block: SourceSetContainer.() -> Unit) = sourceSets.apply(block)

val SourceSetContainer.main: SourceSet get() = getByName("main")
//val SourceSetContainer.test: SourceSet get() = getByName("test")
fun SourceSetContainer.main(block: SourceSet.() -> Unit) = main.apply(block)
//fun SourceSetContainer.test(block: SourceSet.() -> Unit) = test.apply(block)

val SourceSet.kotlin: SourceDirectorySet
    get() = (this as HasConvention).convention.getPlugin<KotlinSourceSet>().kotlin
val SourceSet.groovy: SourceDirectorySet
    get() = (this as HasConvention).convention.getPlugin<GroovySourceSet>().groovy
var SourceDirectorySet.sourceDirs: Iterable<File>
    get() = srcDirs
    set(value) { setSrcDirs(value) }

sourceSets {
    main {
        java.sourceDirs = files("src/main/java")
        groovy.sourceDirs = files("src/main/groovy")
        kotlin.sourceDirs = files("src/main/java")
    }
}


dependencies {
    compile ("com.android.tools.build:gradle:3.1.3")
    compile ("com.android.tools.build:apksig:3.1.3")
//    compile "org.jetbrains.kotlin:kotlin-native-gradle-plugin:0.6.2"
    //    kotlin 标准库
//    compile "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version"
//    kotlin 反射库
//    compile "org.jetbrains.kotlin:kotlin-reflect:$kotlin_version"
//    implementation(kotlin("stdlib", "1.2.31"))
    compile (localGroovy())
    compile (gradleApi())
    compile (kotlin("stdlib"))
}

publishing {
    repositories {
        maven(url = uri("../publish"))
    }
}