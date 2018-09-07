package com.nier.packer.support.ext

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

/**
 * Author fangguodong
 * Date   2018-08-19 7:57 PM
 * E-mail fangguodong@myhexin.com
 */

val sFourSizeByteCache = LinkedBlockingQueue<ByteBuffer>()
val sEightSizeByteCache = LinkedBlockingQueue<ByteBuffer>()
const val CACHE_SIZE = 5
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

fun allocateBuffer(size: Int): ByteBuffer {
    val buffer = allocateFromCache(size) ?: ByteBuffer.allocate(size)
    return buffer.order(ByteOrder.LITTLE_ENDIAN)
}

fun allocateBuffer(size: Int, init: ByteBuffer.(ByteBuffer) -> ByteBuffer): ByteBuffer {
    val buffer = allocateFromCache(size) ?: ByteBuffer.allocate(size)
    buffer.order(ByteOrder.LITTLE_ENDIAN)
    return init(buffer, buffer)
}

fun ByteBuffer.finish(): ByteBuffer {
    return this.flip() as ByteBuffer
}

fun allocateFromCache(size: Int): ByteBuffer? {
    if (size == 4) {
        synchronized(sFourSizeByteCache) {
            return sFourSizeByteCache.poll()
        }
    }

    if (size == 8) {
        synchronized(sEightSizeByteCache) {
            return sEightSizeByteCache.poll()
        }
    }
    return null
}

fun ByteBuffer.recycle() {
    //todo ByteBuffer对象的回收
    if (limit() == 4) {
        synchronized(sFourSizeByteCache) {
            if (sFourSizeByteCache.size < CACHE_SIZE) {
                clear()
                sFourSizeByteCache.add(this)
            }
        }
    }
    if (limit() == 8) {
        synchronized(sEightSizeByteCache) {
            if (sFourSizeByteCache.size < CACHE_SIZE) {
                clear()
                sEightSizeByteCache.add(this)
            }
        }
    }
}
