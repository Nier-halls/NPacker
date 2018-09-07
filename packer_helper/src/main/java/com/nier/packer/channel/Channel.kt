package com.nier.packer.channel

import com.google.gson.Gson
import com.nier.packer.DEFAULT_EXTRA_PAYLOAD_KEY
import com.nier.packer.support.Apk
import com.nier.packer.support.ExtraPayloadHandler
import com.nier.packer.support.IExtraPayloadData
import com.nier.packer.support.IExtraPayloadHandler
import java.io.File

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
    }

    var key = ""
    val fields = HashMap<String, Any?>()


    fun channelKey(_key: String) {
        key = _key
    }

    fun field(field: String, value: Any?) {
        fields[field] = value
    }

    override fun toString(): String {
        return ">>[name = ${name}, key = ${key}, fields = ${fields}]"
    }
}