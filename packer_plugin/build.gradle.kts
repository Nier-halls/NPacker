plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
}

buildscript {
    repositories {
        google()
        jcenter()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.41")
        //idea 有问题用buildSrc读取依赖会报错，但是命令行执行没有问题
//        classpath(Dependencies.kotlin_gradle_plugin)
    }
}

group = Publish.GROUP_ID
version = Publish.VERSION

gradlePlugin {
    plugins {
        register(Publish.PLUGIN_ID) {
            id = Publish.PLUGIN_ID
            implementationClass = "com.nier.packer.NPackerPlugin"
        }
    }
}

task(mutableMapOf<String, Any>("type" to Delete::class.java), "cleanOutput", closureOf<Task> {
    doLast {
        delete("$projectDir${File.separator}out",
                "$projectDir${File.separator}build")
    }
}).also { cleanOutputTask ->
    val cleanTask = tasks.findByName("clean")
            ?: task(mutableMapOf("type" to Delete::class.java), "clean")
    cleanTask.dependsOn(cleanOutputTask)
}


val sourcesJar by tasks.registering(Jar::class) {
    classifier = "sources"
    from(sourceSets.main.get().allSource)
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
            artifact(sourcesJar.get()) {
//                artifactId = Publish.PACKER_PLUGIN_ARTIFACT_ID
            }
        }
    }

    repositories {
        maven(url = Publish.LOCAL_PUBLISH_PATH)
    }
}

dependencies {
    implementation(project(":packer_helper"))
    implementation(localGroovy())
    implementation(gradleApi())
    implementation(Dependencies.android_gradle_plugin)
    implementation(Dependencies.android_apksig)
    implementation(Dependencies.kotlin_stdlib)
}