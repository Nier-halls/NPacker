package com.nier.inject

import java.io.File

/**
 * Author fangguodong
 * Date   2018-08-14 12:03 AM
 * E-mail fangguodong@myhexin.com
 */


fun main(args: Array<String>) {
    findApkEOCDSignature()
}

fun findApkEOCDSignature() {
    val apk = File("./tmpNierDebug.apk")
    println("apk.exists() = ${apk.exists()}")
}