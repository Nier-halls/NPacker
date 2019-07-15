package com.nier.packer.channel

import com.google.gson.Gson
import com.nier.packer.DEFAULT_EXTRA_PAYLOAD_KEY
import com.nier.packer.support.IExtraPayloadData

/**
 * Created by fgd
 * Date 2018/9/5
 */
val gson = Gson()

open class Channel(val name: String) : IExtraPayloadData {

    override fun toByteArray(): ByteArray {
        return gson.toJson(this).toByteArray()
    }

    override fun key(): Int = DEFAULT_EXTRA_PAYLOAD_KEY

    companion object {
        internal fun parse(rawData: ByteArray): Channel {
            return gson.fromJson<Channel>(String(rawData), Channel::class.java)
        }

        val NULL = Channel("NULL")
    }

    var key = ""
    val fields = HashMap<String, ChannelExtraField?>()


    fun channelKey(_key: String) {
        key = _key
    }

    fun buildConfigField(type: String, key: String, value: String) {
        fields[key] = ChannelExtraField(type, key, value)
    }

    override fun toString(): String {
        return ">>[name = $name, key = $key, fields = $fields]"
    }

    fun isValid(): Boolean = name != "NULL"
}

data class ChannelExtraField(val type: String, val name: String, val value: String)