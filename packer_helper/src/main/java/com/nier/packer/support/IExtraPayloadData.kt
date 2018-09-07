package com.nier.packer.support

/**
 * Created by Nier
 * Date 2018/8/20
 */
interface IExtraPayloadData {
    /**
     * 将数据转换成byte数组方便写入到apk中
     */
    fun toByteArray(): ByteArray

    /**
     * 返回主键(payload key)
     */
    fun key(): Int
}