package com.nier.packer.support.ext

import java.nio.ByteBuffer
import java.nio.channels.FileChannel

/**
 * Created by Nier
 * Date 2018/8/23
 */
fun FileChannel.write(block: (Unit) -> ByteBuffer) {
    val byteBuffer = block(Unit)
    write(byteBuffer)
    byteBuffer.recycle()
}