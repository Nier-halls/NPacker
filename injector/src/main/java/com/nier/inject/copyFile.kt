package com.nier.inject

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel



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