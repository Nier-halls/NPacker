package com.nier.packer

/**
 * Created by Nier
 * Date 2018/8/15
 */

open class PackerPluginConvention {
    var apkName: String = "DefauleApk"
    val channels: ArrayList<String> = ArrayList<String>()

    fun addChannels(vararg _channels: String) {
        _channels.forEach {
            channels.add(it)
        }
    }
}