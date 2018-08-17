package com.nier.inject

import java.io.Closeable
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

/**
 * Author fangguodong
 * Date   2018-08-14 12:03 AM
 * E-mail fangguodong@myhexin.com
 */


fun main(args: Array<String>) {
    val apkEOCDSign = findApkEOCDSignaturePosition()
    if (apkEOCDSign < 0) return

}

const val EOCD_SIGNATURE = 0x06054b50


fun findSignBlock(){
    val apk = findApk()
    if (!apk.exists()) return 
}


/**
 * find end of central directory signature
 * signature = 0x06054b50
 */
fun findApkEOCDSignaturePosition(channel: FileChannel): Long {
    val apk = findApk()
    if (!apk.exists()) return -1
    //创建只读流
    RandomAccessFile(apk, "r").channel.use {
        val tempBuffer = ByteBuffer.allocate(4)
        val fileSize = it.size()
        if (!checkApkFileSize()) throw IOException("invalid apk size $fileSize")
        //从后向前找 EOCD Signature
        for (i in fileSize - 4 downTo 0) {
            it.read(tempBuffer, i)
            tempBuffer.order(ByteOrder.LITTLE_ENDIAN)
            if (tempBuffer.getInt(0) == EOCD_SIGNATURE) {

                return i
            }
            tempBuffer.clear()
        }
    }
    return -1
}

fun getCommentLengthFromApk(channel: FileChannel, eocdSignPos: Long): Long {

}

fun findApk(): File = File("${System.getProperty("user.dir")}${File.separator}tmpNierDebug.apk")

fun checkApkFileSize(): Boolean = true