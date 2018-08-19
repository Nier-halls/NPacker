package com.nier.inject

import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

/**
 * Author fangguodong
 * Date   2018-08-19 7:57 PM
 * E-mail fangguodong@myhexin.com
 */
fun readSignBlockValues(channel: FileChannel, signBlockOffset: Long, signBlockSize: Long): Map<Int, ByteBuffer> {
    println("signBlockOffset = $signBlockOffset, signBlockSize = ${signBlockSize.toInt()}")
    channel.position(signBlockOffset + APK_SIGN_BLOCK_SIZE_BYTE_SIZE)
    val payloadBuffer = allocateBuffer(signBlockSize.toInt() - 24)
    channel.read(payloadBuffer)
    payloadBuffer.position(0)
    return readValues(payloadBuffer, HashMap())
}

fun readValues(signBlock: ByteBuffer, values: HashMap<Int, ByteBuffer>): HashMap<Int, ByteBuffer> {
    println("apk sign block remain -> ${signBlock.remaining()}")
    if (signBlock.remaining() < SIGN_BLOCK_PAYLOAD_VALUE_LENGTH_BYTE_SIZE) {
        return values
    }
    val valueSize = signBlock.long
    if (signBlock.remaining() < valueSize || valueSize < SIGN_BLOCK_PAYLOAD_ID_BYTE_SIZE) {
        throw IOException("Invalid sign block payload values.")
    }
    val id = signBlock.int

    val content = signBlock.slice(valueSize.toInt() - SIGN_BLOCK_PAYLOAD_ID_BYTE_SIZE)
    values[id] = content
    return readValues(signBlock, values)
}

/**
 * 从原始的Buffer中切割一段一下，并且位移position
 */
fun ByteBuffer.slice(length: Int): ByteBuffer {
    val slicedBuffer = slice(position(), position() + length)
    position(position() + length)
    return slicedBuffer
}


fun ByteBuffer.slice(start: Int, end: Int): ByteBuffer {
    if (start < 0 || end < start) {
        throw IllegalArgumentException("invalid position start($start) or end($end)")
    }
    if (capacity() < end) {
        throw  IllegalArgumentException("end($end) is out of capacity(${capacity()})")
    }
    val originPosition = position()
    val originLimit = limit()
    try {
        position(start)
        limit(end)
        val slicedBuffer = slice()
        slicedBuffer.order(order())
        return slicedBuffer
    } finally {
        position(originPosition)
        limit(originLimit)
    }
}