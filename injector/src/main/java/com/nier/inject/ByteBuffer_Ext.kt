package com.nier.inject

import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

/**
 * Author fangguodong
 * Date   2018-08-19 7:57 PM
 * E-mail fangguodong@myhexin.com
 */

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

/**
 * todo 添加对象池，缓存4 8的ByteBuffer
 */
fun allocateBuffer(size: Int): ByteBuffer {
    val buffer = ByteBuffer.allocate(size)
    return buffer.order(ByteOrder.LITTLE_ENDIAN)
}