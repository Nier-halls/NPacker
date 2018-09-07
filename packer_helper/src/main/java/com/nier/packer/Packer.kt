package com.nier.packer

import com.nier.packer.channel.Channel
import com.nier.packer.support.Apk
import com.nier.packer.support.IExtraPayloadData
import java.io.File

/**
 * Created by fgd
 * Date 2018/9/5
 */
class Packer private constructor() {

    private lateinit var apk: Apk

    companion object {
        fun init(apkFile: File): Packer {
            val packer = Packer()
            packer.apk = Apk.createApk(apkFile)
            return packer
        }
    }


    private var currentChannel: Channel? = null

    fun injectData(data: IExtraPayloadData) {
        apk.injectExtraData(data)
    }

    private fun getChannel(): Channel? {
        if (currentChannel == null) {
            currentChannel = Channel.parse(apk.getExtraData(DEFAULT_EXTRA_PAYLOAD_KEY)
                    ?: return null)
        }
        return currentChannel
    }

    fun channelName(): String? = getChannel()?.name

    fun channelCode(): String? = getChannel()?.key

    fun extraData(key: String): Any? = getChannel()?.fields?.get(key)

}