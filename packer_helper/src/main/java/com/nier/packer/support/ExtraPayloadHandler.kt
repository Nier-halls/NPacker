package com.nier.packer.support

import com.nier.packer.APK_EXTRA_MAGIC
import com.nier.packer.support.ext.allocateBuffer
import com.nier.packer.support.ext.finish
import java.nio.ByteBuffer

/**
 * Created by Nier
 * Date 2018/8/23
 */

internal class ExtraPayloadHandler : IExtraPayloadHandler {
    /**
     * PLUGIN BLOCK LAYOUT
     * OFFSET    DATA TYPE                     DESCRIPTION
     * @+0       payload content length        payload length int 4 bytes
     * @+4       magic string                  magic string 16 bytes
     * @+20      payload key                   same as out primary key
     * @+24      payload content length        payload length int 4 bytes
     * @+28      payload content               payload data bytes
     * @-4       payload content               payload length same as begin of
     *
     * 自定义协议防止别人篡改（渠道）数据
     */
    override fun wrap(payloadEntry: IExtraPayloadData): ByteBuffer {
        val rawContent = payloadEntry.toByteArray()
        val magicHeader = APK_EXTRA_MAGIC.toByteArray(Charsets.UTF_8)
        val payloadKey = payloadEntry.key()
        val contentLength = 4 + //size of length (head)
                magicHeader.size + //size of magic
                4 + //size of key
                rawContent.size + //size of content
                4 //size of length (end)

        return allocateBuffer(contentLength) {
            putInt(contentLength)//extraData数据的长度
            put(magicHeader)//魔数
            putInt(payloadKey)//extraData对应Payload的key（payload的key）
            put(rawContent)//extraData数据
            putInt(contentLength)//extraData数据的长度
            finish()
        }
    }

    /**
     * 检查获取渠道信息
     * 感觉是不是检查的有点多了....
     */
    override fun parse(rawPayload: ByteBuffer, payloaKey: Int): ByteArray? {
        val minLength = APK_EXTRA_MAGIC.length + 4 + 4
        if (rawPayload.limit() < minLength) {
            return null
        }

        val beginOfLength = rawPayload.int
        var remainLength = beginOfLength - 4
        //check begin of length
        if (remainLength != rawPayload.remaining()) {
            println("Invalid extra data...check length failed")
            return null
        }

        //check magic
        val magicByte = ByteArray(APK_EXTRA_MAGIC.length)
        rawPayload.get(magicByte)
        if (APK_EXTRA_MAGIC != String(magicByte)) {
            println("Invalid extra data...check magic failed")
            return null
        }
        remainLength -= APK_EXTRA_MAGIC.length

        //check payload key
        val realPayloadKey = rawPayload.int
        if (payloaKey != realPayloadKey) {
            println("Invalid extra data...check payload key failed")
            return null
        }
        remainLength -= 4 //subtract size of key

        val contentLength = remainLength - 4 //subtract size of payload length (end)
        val result = ByteArray(contentLength)
        rawPayload.get(result)

        //check end of length
        val endOfLength = rawPayload.int
        if (beginOfLength != endOfLength) {
            println("Invalid extra data...check length (end) failed")
            return null
        }
        return result
    }

}