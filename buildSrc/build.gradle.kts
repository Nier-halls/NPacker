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
    val cleanTask = tasks.findByName("clean")
            ?: task(mutableMapOf("type" to Delete::class.java), "clean")
    cleanTask.dependsOn(cleanOutputTask)
}
