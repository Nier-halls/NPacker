package com.nier.packer

import com.android.apksig.ApkVerifier
import java.io.File
import java.io.FileInputStream
import java.nio.channels.FileChannel

/**
 * Created by Nier
 * Date 2018/7/26
 */

fun copyFile(source: File, destination: File): Boolean {
    if (!source.exists()) {
        return false
    }
    if (!destination.exists()) {
        destination.createNewFile()
    }
    var sourceChannel: FileChannel? = null
    var destinationChannel: FileChannel? = null

    try {
        sourceChannel = FileInputStream(source).channel
        destinationChannel = FileInputStream(destination).channel
        destinationChannel.transferFrom(sourceChannel!!, 0, sourceChannel.size())
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    } finally {
        sourceChannel?.let {
            sourceChannel.close()
        }
        destinationChannel?.let {
            destinationChannel.close()
        }
    }
    return true
}

fun verfyApk(apkFile: File): Boolean {
    if (!apkFile.exists()) {
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
        false
    }

}