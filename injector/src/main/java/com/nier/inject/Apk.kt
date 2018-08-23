package com.nier.inject

import java.io.File
import java.io.RandomAccessFile
import java.nio.channels.FileChannel

/**
 * Created by Nier
 * Date 2018/8/20
 */
class Apk private constructor(var sourceDir: File) {

    internal interface IChannelAction {
        fun action(fileChannel: FileChannel)
    }

    internal var mCentralDirectoryStartOffset: Long = -1
    internal var mSignBlockOffset: Long = -1
    internal var mSignBlockSize: Long = -1
    internal var mExtraPayloadHandler: IExtraPayloadHandler = ExtraPayloadHandlerTest()

    companion object {
        fun createApk(sourceDir: File): Apk {
            if (!sourceDir.exists()) println("apk not found.")
            // todo check apk file valid
            return Apk(sourceDir).init()
        }

        fun createApk(sourceDir: File, extraPayloadHander: IExtraPayloadHandler): Apk {
            if (!sourceDir.exists()) println("apk not found.")
            // todo check apk file valid
            val apk = Apk(sourceDir)
            apk.mExtraPayloadHandler = extraPayloadHander
            apk.init()
            return apk
        }
    }

    fun invalid(): Boolean {
        return mCentralDirectoryStartOffset < 0 ||
                mSignBlockOffset < 0 ||
                mSignBlockSize < 0
    }

    private fun init(): Apk {
        channel {
            mCentralDirectoryStartOffset = findCentralDirectoryStartOffset(it, findApkEOCDSignatureOffset(it))
            val (signBlockOffset, signBlockSize) = getSignBlockOffsetAndSize(it, mCentralDirectoryStartOffset)
            mSignBlockOffset = signBlockOffset
            mSignBlockSize = signBlockSize
        }
        return this
    }

    /**
     * 方便回收资源
     */
    inline fun channel(action: (FileChannel) -> Unit) {
        RandomAccessFile(sourceDir, "rw").use {
            it.channel.use {
                action(it)
            }
        }
    }

    fun getExtraData(){
        getPayloadById(this,)


    }
}

//fun main(args: Array<String>) {
////    fun findApk(): File = File("${System.getProperty("user.dir")}${File.separator}tmpNierDebug.apk")
////    println(readPayload(Apk.createApk(findApk())))
//
//
////    val map1 = hashMapOf("1" to 1, "2" to 2)
////    val map2 = hashMapOf("3" to 3, "2" to 4)
////    map1.putAll(map2)
////    println(map1)
////    val s: String = "Packer Ng Sig V2"
////    s.toByteArray(Charsets.UTF_8)
////
////    val buffer = allocateBuffer(20)
////    buffer.put("1234567".toByteArray())
////    println(buffer)
////    buffer.flip()
////    println(buffer)
//
//
//    addPayload(Apk.createApk(File("${System.getProperty("user.dir")}${File.separator}tmpNierDebug.apk")),
//            object : IApkExtraData {
//                override fun key(): Int {
//                    return 1541
//                }
//
//                override fun flat(): ByteArray {
//                    return "1234567890".toByteArray()
//                }
//            })
//
//    println("====================================================")
//
//    fun findApk(): File = File("${System.getProperty("user.dir")}${File.separator}tmpNierDebug.apk")
//    println(readPayload(Apk.createApk(findApk())))
//
//
////    println(verifyApk(File("${System.getProperty("user.dir")}${File.separator}tmpNierDebug.apk")))
//
////    fun findApk(): File = File("${System.getProperty("user.dir")}${File.separator}tmpNierDebug.apk")
////    println(readPayload(Apk.createApk(findApk())))
//
//}





