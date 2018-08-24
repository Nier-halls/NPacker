package com.nier.inject

import java.nio.ByteBuffer

/**
 * Created by Nier
 * Date 2018/8/23
 */

interface IExtraPayloadHandler {
    /**
     * 根据协议拼装写入到payload中的数据
     */
    fun wrap(payloadEntry: IExtraPayload): ByteBuffer

    /**
     * 解析从payload中获取的数据
     */
    fun parse(rawPayload: ByteBuffer): ByteArray
}