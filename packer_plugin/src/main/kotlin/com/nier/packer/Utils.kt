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

//fun copyFile(source: File, destination: File): Boolean {
//    if (!source.exists()) {
//        return false
//    }
//    if (!destination.exists()) {
//        destination.createNewFile()
//    }
//    var sourceChannel: FileChannel? = null
//    var destinationChannel: FileChannel? = null
//    try {
//        sourceChannel = FileInputStream(source).channel
//        destinationChannel = RandomAccessFile(destination, "rw").channel
//        destinationChannel.transferFrom(sourceChannel!!, 0, sourceChannel.size())
//    } catch (e: Exception) {
//        e.printStackTrace()
//        return false
//    } finally {
//        sourceChannel?.close()// >>>> 'let'和'?.'有什么区别
//        destinationChannel?.let {
//            it.close()
//        }
//    }
//    return true
//}

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