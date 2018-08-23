package com.nier.inject

import java.nio.ByteBuffer

/**
 * Created by Nier
 * Date 2018/8/23
 */

internal class ExtraPayloadHandler : IExtraPayloadHandler {
    /**
     * PLUGIN BLOCK LAYOUT
     * OFFSET    DATA TYPE                     DESCRIPTION
     * @+0       magic string                  magic string 16 bytes
     * @+16      payload key                   same as out primary key
     * @+20      payload content length        payload length int 4 bytes
     * @+24      payload content               payload data bytes
     *
     * 自定义协议防止别人篡改（渠道）数据
     */
    override fun wrap(payloadEntry: IExtraPayload): ByteBuffer {
        val rawContent = payloadEntry.flat()
        val magicHeader = APK_EXTRA_MAGIC.toByteArray(Charsets.UTF_8)
        val contentLength = rawContent.size
        val payloadKey = payloadEntry.key()
        val blockSize = magicHeader.size + 4 + 4 + contentLength
        val buffer = allocateBuffer(rawContent.size)
        buffer.put(magicHeader)
        buffer.putInt(payloadKey)
        buffer.putInt(contentLength)
        buffer.put(rawContent)
        buffer.flip()
        return buffer
    }

    override fun parse(rawPayload: ByteBuffer): ByteArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}