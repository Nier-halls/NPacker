package com.nier.inject

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
 *
 * todo ByteBuffer object pool
 */


fun main(args: Array<String>) {

    val apk = findApk()
    if (!apk.exists()) println("apk not found.")
    //创建只读流
    RandomAccessFile(apk, "r").channel.use {
        val apkEOCDSignOffset = findApkEOCDSignatureOffset(it)
        val cdso = findCentralDirectoryStartOffset(it, apkEOCDSignOffset)
        val (signBlockOffset, signBlockSize) = getSignBlockOffsetAndSize(it, cdso)
        val signBlockValues = readSignBlockValues(it, signBlockOffset, signBlockSize)
        println("signBlockValues ===> $signBlockValues")
    }
}


/**
 * find end of central directory signature
 * signature = 0x06054b50
 */
fun findApkEOCDSignatureOffset(channel: FileChannel): Long {
    val tempBuffer = ByteBuffer.allocate(END_OF_CENTRAL_DIRECTORY_SIGNATURE_BYTE_SZIE)
    val fileSize = channel.size()
    if (!checkApkFileSize()) throw IOException("invalid apk size $fileSize")
    //从后向前找 EOCD Signature
    for (i in fileSize - END_OF_CENTRAL_DIRECTORY_SIGNATURE_BYTE_SZIE downTo 0) {
        channel.read(tempBuffer, i)
        tempBuffer.order(ByteOrder.LITTLE_ENDIAN)
        if (tempBuffer.getInt(0) == END_OF_CENTRAL_DIRECTORY_SIGNATURE) {
            println("find ecod signature success, position=$i")
            return i
        }
        tempBuffer.clear()
    }
    throw IOException("can not find apk end of central directory signature")
}

//todo
// 1.find comments
// 2.find central directory start offset

fun findCentralDirectoryStartOffset(channel: FileChannel, eocdOffset: Long): Long {

    // End of central directory record (EOCD)
    // Offset    Bytes     Description[23]
    // 0           4       End of central directory signature = 0x06054b50
    // 4           2       Number of this disk
    // 6           2       Disk where central directory starts
    // 8           2       Number of central directory records on this disk
    // 10          2       Total number of central directory records
    // 12          4       Size of central directory (bytes)
    // 16          4       Offset of start of central directory, relative to start of archive
    // 20          2       Comment length (n)
    // 22          n       Comment
    val cdsoOffset = eocdOffset +
            END_OF_CENTRAL_DIRECTORY_SIGNATURE_BYTE_SZIE +
            NUMBER_OF_THIS_DISK_BYTE_SIZE +
            DISK_WHERE_CENTRAL_DIRECTORY_STARTS_BYTE_SIZE +
            NUMBER_OF_CENTRAL_DIRECTORY_RECORDS_BYTE_SIZE +
            TOTAL_NUMBER_OF_CENTRAL_DIRECTORY_RECORDS_BYTE_SIZE +
            SIZE_OF_CENTRAL_DIRECTORY_BYTE_SIZE

    channel.position(cdsoOffset)
    val tempBuffer = allocateBuffer(OFFSET_OF_START_OF_CENTRAL_DIRECTORY_BYTE_SIZE)
    channel.read(tempBuffer)
    val cdso = tempBuffer.getInt(0)
    println("cdsoOffsetValue = $cdso")
    if (cdso >= eocdOffset || cdso <= 0) {
        throw IOException("cdso=$cdso is invalid, cdso > apkEOCDSignOffset=$eocdOffset or cdso <=0.")
    }
    return cdso.toLong()
}


fun getSignBlockOffsetAndSize(channel: FileChannel, centralDirectoryStartOffset: Long): Pair<Long, Long> {

    // OFFSET       DATA TYPE  DESCRIPTION
    // @+0  bytes uint64:    size in bytes (excluding this field)
    // @+8  bytes payload
    // @-24 bytes uint64:    size in bytes (same as the one above)
    // @-16 bytes uint128:   magic
    channel.position(centralDirectoryStartOffset.toLong() -
            APK_SIGN_BLOCK_SIZE_BYTE_SIZE -
            APK_SIGN_BLOCK_MAGIC_NUM_BYTE_SIZE * 2)

    val tempBuffer = allocateBuffer(APK_SIGN_BLOCK_SIZE_BYTE_SIZE + APK_SIGN_BLOCK_MAGIC_NUM_BYTE_SIZE * 2)
    channel.read(tempBuffer)
    val signBlockMagicLow = tempBuffer.getLong(APK_SIGN_BLOCK_MAGIC_NUM_BYTE_SIZE)
    val signBlockMagicHigh = tempBuffer.getLong(APK_SIGN_BLOCK_MAGIC_NUM_BYTE_SIZE * 2)
    if (signBlockMagicLow == APK_SIGN_BLOCK_MAGIC_LOW &&
            signBlockMagicHigh == APK_SIGN_BLOCK_MAGIC_HIGH) {
        println("check apk sign block magic num success.")
    } else {
        println("check apk sign block magic num failed, signBlockMagicLow=$signBlockMagicLow  signBlockMagicHigh=$signBlockMagicHigh.")
        println("APK_SIGN_BLOCK_MAGIC_LOW=$APK_SIGN_BLOCK_MAGIC_LOW  APK_SIGN_BLOCK_MAGIC_HIGH=$APK_SIGN_BLOCK_MAGIC_HIGH.")
        return Pair(-1, 0)
    }
    val signBlockSize = tempBuffer.getLong(0)
    //size in bytes (excluding this field)
    //size不包括第一个size字段所占的长度8所以计算offset要吧这个8计算在内
    val signBlockOffset = centralDirectoryStartOffset - signBlockSize - APK_SIGN_BLOCK_SIZE_BYTE_SIZE
    if (signBlockOffset < 0) {
        throw IOException("Invalid sign block offset($signBlockOffset).")
    }
    return Pair(signBlockOffset, signBlockSize)
}


fun findApk(): File = File("${System.getProperty("user.dir")}${File.separator}tmpNierDebug.apk")

fun checkApkFileSize(): Boolean = true

fun allocateBuffer(size: Int): ByteBuffer {
    val buffer = ByteBuffer.allocate(size)
    return buffer.order(ByteOrder.LITTLE_ENDIAN)
}

