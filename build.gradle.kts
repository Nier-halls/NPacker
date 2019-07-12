allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
    }
}

afterEvaluate {
    val cleanTask = tasks.findByName("clean")
            ?: task(mutableMapOf("type" to Delete::class.java), "clean", closureOf<Task> {
                doLast {
                    println("Clean task start.")
                }

            })

    val cleanPublishOutputTask = task("cleanPublish") {
        doLast(closureOf<Task> {
            println("clean publish dir.")
            delete("$projectDir${File.separator}publish",
                    "$projectDir${File.separator}build")
        })
    }

    cleanTask.dependsOn(cleanPublishOutputTask)
}

project.childProjects["packer_plugin"]?.afterEvaluate {
    task("publishPlugin") {
        dependsOn(project.tasks.findByName("publish"))
        doLast {
            println("Start publish packer plugin.")
        }
    }
}

project.childProjects["packer_helper"]?.afterEvaluate {
    task("publishHelper") {
        dependsOn(project.tasks.findByName("publish"))
        doLast {
            println("Start publish packer helper.")
        }
    }
}


