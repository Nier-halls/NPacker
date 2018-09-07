package com.nier.packer.support

import com.android.apksig.ApkVerifier
import com.nier.packer.support.ext.allocateBuffer
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

/**
 * Created by Nier
 * Date 2018/8/23
 */


/**
 * 获取central directory start offset的偏移量，
 * central directory start offset之前紧接着就是
 * 前面模块SignBlock
 */
internal fun findCentralDirectoryStartOffset(channel: FileChannel, eocdOffset: Long): Long {

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
    val cdsoOffset = calculateCentralDirectoryOffset(eocdOffset)

    channel.position(cdsoOffset)
    val tempBuffer = allocateBuffer(com.nier.packer.OFFSET_OF_START_OF_CENTRAL_DIRECTORY_BYTE_SIZE)
    channel.read(tempBuffer)
    val cdso = tempBuffer.getInt(0)
    println("cdsoOffsetValue = $cdso")
    if (cdso >= eocdOffset || cdso <= 0) {
        throw IOException("cdso=$cdso is invalid, cdso > apkEOCDSignOffset=$eocdOffset or cdso <=0.")
    }
    return cdso.toLong()
}

/**
 * 获取SignBlock的偏移地址和长度
 */
internal fun getSignBlockOffsetAndSize(apkChannel: FileChannel, centralDirectoryStartOffset: Long): Pair<Long, Long> {

    // OFFSET       DATA TYPE  DESCRIPTION
    // @+0  bytes uint64:    size in bytes (excluding this field)
    // @+8  bytes payload
    // @-24 bytes uint64:    size in bytes (same as the one above)
    // @-16 bytes uint128:   magic
    apkChannel.position(centralDirectoryStartOffset.toLong() -
            com.nier.packer.APK_SIGN_BLOCK_SIZE_BYTE_SIZE -
            com.nier.packer.APK_SIGN_BLOCK_MAGIC_NUM_BYTE_SIZE * 2)

    val tempBuffer = allocateBuffer(com.nier.packer.APK_SIGN_BLOCK_SIZE_BYTE_SIZE + com.nier.packer.APK_SIGN_BLOCK_MAGIC_NUM_BYTE_SIZE * 2)
    apkChannel.read(tempBuffer)
    val signBlockMagicLow = tempBuffer.getLong(com.nier.packer.APK_SIGN_BLOCK_MAGIC_NUM_BYTE_SIZE)
    val signBlockMagicHigh = tempBuffer.getLong(com.nier.packer.APK_SIGN_BLOCK_MAGIC_NUM_BYTE_SIZE * 2)
    if (signBlockMagicLow == com.nier.packer.APK_SIGN_BLOCK_MAGIC_LOW &&
            signBlockMagicHigh == com.nier.packer.APK_SIGN_BLOCK_MAGIC_HIGH) {
        println("check apk sign block magic num success.")
    } else {
        println("check apk sign block magic num failed, signBlockMagicLow=$signBlockMagicLow  signBlockMagicHigh=$signBlockMagicHigh.")
        println("APK_SIGN_BLOCK_MAGIC_LOW=${com.nier.packer.APK_SIGN_BLOCK_MAGIC_LOW}  APK_SIGN_BLOCK_MAGIC_HIGH=${com.nier.packer.APK_SIGN_BLOCK_MAGIC_HIGH}.")
        return Pair(-1, 0)
    }
    val signBlockSize = tempBuffer.getLong(0)
    //size in bytes (excluding this field)
    //size不包括第一个size字段所占的长度8所以计算offset要吧这个8计算在内
    val signBlockOffset = centralDirectoryStartOffset - signBlockSize - com.nier.packer.APK_SIGN_BLOCK_SIZE_BYTE_SIZE
    if (signBlockOffset < 0) {
        throw IOException("Invalid sign block offset($signBlockOffset).")
    }
    return Pair(signBlockOffset, signBlockSize)
}


/**
 *
 * 获取ZIP的 end of central directory signature，该字段靠后一般从后往前获取
 * signature = 0x06054b50
 */
internal fun findApkEOCDSignatureOffset(apkFileChannel: FileChannel): Long {
    val tempBuffer = ByteBuffer.allocate(com.nier.packer.END_OF_CENTRAL_DIRECTORY_SIGNATURE_BYTE_SZIE)
    val fileSize = apkFileChannel.size()
    if (!checkApkFileSize()) throw IOException("invalid apk size $fileSize")
    //从后向前找 EOCD Signature
    for (i in fileSize - com.nier.packer.END_OF_CENTRAL_DIRECTORY_SIGNATURE_BYTE_SZIE downTo 0) {
        apkFileChannel.read(tempBuffer, i)
        tempBuffer.order(ByteOrder.LITTLE_ENDIAN)
        if (tempBuffer.getInt(0) == com.nier.packer.END_OF_CENTRAL_DIRECTORY_SIGNATURE) {
            println("find ecod signature success, position=$i")
            return i
        }
        tempBuffer.clear()
    }
    throw IOException("can not find apk end of central directory signature")
}

internal fun verifyApk(apkFile: File): Boolean {
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

/**
 * 检查 apk尺寸等是否有效（待完善）
 */
internal fun checkApkFileSize(): Boolean = true

internal fun calculateCentralDirectoryOffset(endOfCentralDirectoryOffset: Long): Long {
    return endOfCentralDirectoryOffset +
            com.nier.packer.END_OF_CENTRAL_DIRECTORY_SIGNATURE_BYTE_SZIE +
            com.nier.packer.NUMBER_OF_THIS_DISK_BYTE_SIZE +
            com.nier.packer.DISK_WHERE_CENTRAL_DIRECTORY_STARTS_BYTE_SIZE +
            com.nier.packer.NUMBER_OF_CENTRAL_DIRECTORY_RECORDS_BYTE_SIZE +
            com.nier.packer.TOTAL_NUMBER_OF_CENTRAL_DIRECTORY_RECORDS_BYTE_SIZE +
            com.nier.packer.SIZE_OF_CENTRAL_DIRECTORY_BYTE_SIZE
}