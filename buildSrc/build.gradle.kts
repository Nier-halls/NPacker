plugins {
    `kotlin-dsl`
}

repositories {
    jcenter()
}

task(mutableMapOf<String, Any>("type" to Delete::class.java), "cleanOutput", closureOf<Task> {
    doLast {
        delete("$projectDir${File.separator}build")
    }
}).also { cleanOutputTask ->
    tasks.named("clean").get().dependsOn(cleanOutputTask)
}
