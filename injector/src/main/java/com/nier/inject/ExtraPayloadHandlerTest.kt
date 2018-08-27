package com.nier.inject

import java.nio.ByteBuffer

/**
 * Created by Nier
 * Date 2018/8/23
 *
 * 测试用的没有任何协议，直接读直接取
 */
internal class ExtraPayloadHandlerTest : IExtraPayloadHandler {
    override fun wrap(payload: IExtraPayload): ByteBuffer {
        val rawContent = payload.flat()
//    val magicHeader = APK_EXTRA_MAGIC.toByteArray(Charsets.UTF_8)
//    val contentLength = rawContent.size
//    val payloadKey = data.key()
//    val blockSize = magicHeader.size + 4 + 4 + contentLength
        val buffer = allocateBuffer(rawContent.size)
//    buffer.put(magicHeader)
//    buffer.putInt(payloadKey)
//    buffer.putInt(contentLength)
        buffer.put(rawContent)
        buffer.flip()
        return buffer
    }

    override fun parse(rawPayload: ByteBuffer, payloaKey: Int): ByteArray {
        val len = rawPayload.limit()
        val byte: ByteArray = ByteArray(len)
        rawPayload.get(byte)
        return byte
    }

}
