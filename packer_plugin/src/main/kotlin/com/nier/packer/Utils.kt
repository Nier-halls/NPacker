package com.nier.packer

import com.android.apksig.ApkVerifier
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel

/**
 * Created by Nier
 * Date 2018/7/26
 */

@Throws(IOException::class)
fun copyFile(src: File, dest: File) {
    if (!dest.exists()) {
        dest.createNewFile()
    }
    var source: FileChannel? = null
    var destination: FileChannel? = null
    try {
        source = FileInputStream(src).channel
        destination = FileOutputStream(dest).channel
        destination!!.transferFrom(source, 0, source!!.size())
    } finally {
        source?.close()
        destination?.close()
    }
}

fun verifyApk(apkFile: File): Boolean {
    if (!apkFile.exists()) {
        println("apkFile no exit.")
        return false
    }
    val apkVerifier = ApkVerifier.Builder(apkFile).build()

    return try {
        val result = apkVerifier.verify()
        result.isVerified &&
                result.isVerifiedUsingV1Scheme &&
                result.isVerifiedUsingV2Scheme
    } catch (e: Exception) {
        e.printStackTrace()
        println("verifyApk on error. errorMessage -> ${e.message}")
        false
    }

}