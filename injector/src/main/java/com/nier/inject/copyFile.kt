package com.nier.inject

import java.io.File
import java.io.FileInputStream
import java.io.RandomAccessFile
import java.nio.channels.FileChannel

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
        destinationChannel = RandomAccessFile(destination, "rw").channel
        destinationChannel.transferFrom(sourceChannel!!, 0, sourceChannel.size())
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    } finally {
        sourceChannel?.close()// >>>> 'let'和'?.'有什么区别
        destinationChannel?.let {
            it.close()
        }
    }
    return true
}