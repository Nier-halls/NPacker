package com.nier.inject

import java.nio.ByteBuffer

/**
 * Created by Nier
 * Date 2018/8/23
 */

interface IExtraPayloadHandler {

    fun wrap(payloadEntry: IExtraPayload): ByteBuffer

    fun parse(rawPayload: ByteBuffer): ByteArray
}