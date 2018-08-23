package com.nier.inject

import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

/**
 * Created by Nier
 * Date 2018/8/20
 */
interface IExtraPayload {
    /**
     * 将数据转换成byte数组方便写入到apk中
     */
    fun flat(): ByteArray

    /**
     * 返回主键(payload key)
     */
    fun key(): Int
}