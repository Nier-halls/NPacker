plugins {
    java
    `kotlin-dsl`
    `maven-publish`
}

buildscript {
    repositories {
        mavenCentral()
        google()
        jcenter()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.41")
        //idea 有问题用buildSrc读取依赖会报错，但是命令行执行没有问题
//        classpath(Dependencies.kotlin_gradle_plugin)
    }
}

group = Publish.GROUP_ID
version = Publish.VERSION

val sourcesJar by tasks.registering(Jar::class) {
    classifier = "sources"
    from(sourceSets.main.get().allSource)
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
            artifact(sourcesJar.get()) {
                //                artifactId = Publish.PACKER_HELPER_ARTIFACT_ID
            }
        }
    }

    repositories {
        maven(url = Publish.LOCAL_PUBLISH_PATH)
    }
}

task(mutableMapOf<String, Any>("type" to Delete::class.java), "cleanOutput", closureOf<Task> {
    doLast {
        delete("$projectDir${File.separator}out",
                "$projectDir${File.separator}build")
    }
}).also { cleanOutputTask ->
    tasks.named("clean").get().dependsOn(cleanOutputTask)
}

dependencies {
    implementation(localGroovy())
    implementation(gradleApi())
    implementation(Dependencies.kotlin_stdlib)
    implementation(Dependencies.android_apksig)
    implementation(Dependencies.gson)
}
