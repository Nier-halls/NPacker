import java.io.File
import java.io.FileInputStream
import java.util.*

/**
 * Created by fgd
 * Date 2019/7/9
 */

private const val PROPERTIES_FILE_PATH = "./version.properties"

private var versionProperties: Properties? = null
    get() {
        if (field == null) {
            val file = File(PROPERTIES_FILE_PATH)
            println("properties file >>>> ${file.absolutePath}")
            field = FileInputStream(file).use {
                Properties().apply {
                    load(it)
                }
            }
        }
        return field
    }

fun version(dependency: String): String {
    val targetVersion = versionProperties?.get(dependency)
    if (targetVersion is String) {
        println("get version $dependency -> $targetVersion.")
        return targetVersion
    }
    return ""
}

object Dependencies {
//    val android_gradle_plugin = "com.android.tools.build:gradle:${version("android_gradle_plugin")}"
//    val android_apksig = "com.android.tools.build:apksig:${version("android_gradle_plugin")}"
//    val kotlin_gradle_plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${version("kotlin")}"
//    val kotlin_stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${version("kotlin")}"
//    val anko = "com.google.code.gson:gson:${version("anko")}"
//    val gson = "com.google.code.gson:gson:${version("gson")}"

    val android_gradle_plugin = "com.android.tools.build:gradle:3.4.1"
    val android_apksig = "com.android.tools.build:apksig:3.4.1"
    val kotlin_gradle_plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.41"
    val kotlin_stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.3.41"
    val gson = "com.google.code.gson:gson:2.8.2"
}