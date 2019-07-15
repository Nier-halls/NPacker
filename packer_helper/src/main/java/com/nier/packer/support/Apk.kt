package com.nier.packer.support

import com.nier.packer.DEFAULT_EXTRA_PAYLOAD_KEY
import java.io.File
import java.io.RandomAccessFile
import java.nio.channels.FileChannel

/**
 * Created by Nier
 * Date 2018/8/20
 */
internal class Apk private constructor(var source: File) {

    internal interface IChannelAction {
        fun action(fileChannel: FileChannel)
    }

    internal var mCentralDirectoryStartOffset: Long = -1
    internal var mSignBlockOffset: Long = -1
    internal var mSignBlockSize: Long = -1
    internal lateinit var mExtraPayloadProtocol: IExtraPayloadHandler

    companion object {
        internal fun createApk(sourceDir: File, extraPayloadHandler: IExtraPayloadHandler = DefaultExtraPayloadHandler()): Apk {
            if (!sourceDir.exists()) println("apk not found.")
            return Apk(sourceDir).apply {
                mExtraPayloadProtocol = extraPayloadHandler
                init()
            }
        }
    }

    internal fun invalid(): Boolean {
        return mCentralDirectoryStartOffset < 0 ||
                mSignBlockOffset < 0 ||
                mSignBlockSize < 0
    }

    private fun init(): Apk {
        readOnlyChannel {
            mCentralDirectoryStartOffset = findCentralDirectoryStartOffset(this, findApkEOCDSignatureOffset(this))
            val (signBlockOffset, signBlockSize) = getSignBlockOffsetAndSize(this, mCentralDirectoryStartOffset)
            mSignBlockOffset = signBlockOffset
            mSignBlockSize = signBlockSize
        }
        return this
    }

    /**
     * 方便回收资源
     */
    internal inline fun channel(action: FileChannel.(FileChannel) -> Unit) {
        RandomAccessFile(source, "rw").use { randomAccessFile ->
            randomAccessFile.channel.use {
                action(it, it)
            }
        }
    }

    /**
     * 方便回收资源
     */
    internal inline fun readOnlyChannel(action: FileChannel.(FileChannel) -> Unit) {
        RandomAccessFile(source, "r").use { randomAccessFile ->
            randomAccessFile.channel.use {
                action(it, it)
            }
        }
    }

    fun getExtraData(key: Int = DEFAULT_EXTRA_PAYLOAD_KEY): ByteArray? {
        val extra = getPayloadById(this, key)
        extra?.let {
            return mExtraPayloadProtocol.parse(extra, key)
        }
        return null
    }

    fun injectExtraData(data: IExtraPayloadData) {
        addPayload(this, data)
    }
}

//fun main(args: Array<String>) {
//    val target = File("F:\\AndroidProject\\GradlePlugin\\packer", "tmpFgdDebug.apk")
//    val source = File("F:\\AndroidProject\\GradlePlugin\\app\\build\\outputs\\apk\\fgd\\debug", "app-fgd-debug.apk")
//    copyFileError(source, target)
//    println("verify = ${verifyApk(target)}")
//    Apk.createApk(target).injectExtraData(object : IExtraPayload {
//        override fun key(): Int {
//            return DEFAULT_EXTRA_PAYLOAD_KEY
//        }
//
//        override fun flat(): ByteArray {
//            return "new version fgd hahahahahaha !@#$%^&*()_+ant".toByteArray(Charsets.UTF_8)
//                    ?: throw IllegalArgumentException("Unknow extra payload data.")
//        }
//    })
//}

//fun main(args: Array<String>) {
////    fun findApk(): File = File("${System.getProperty("user.dir")}${File.separator}tmpNierDebug.apk")
////    println(readPayload(Apk.createApk(findApk())))
//
//
////    val map1 = hashMapOf("1" to 1, "2" to 2)
////    val map2 = hashMapOf("3" to 3, "2" to 4)
////    map1.putAll(map2)
////    println(map1)
////    val s: String = "NPacker Ng Sig V2"
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





