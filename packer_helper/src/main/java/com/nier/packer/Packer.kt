package com.nier.packer

import com.nier.packer.channel.Channel
import com.nier.packer.support.Apk
import com.nier.packer.support.IExtraPayloadData
import java.io.File

/**
 * Created by fgd
 * Date 2018/9/5
 */


const val TYPE_STRING = "String"

const val TYPE_INT = "Int"
const val TYPE_INT_LOWER_CASE = "int"
const val TYPE_INTEGER = "Integer"

const val TYPE_LONG = "Long"
const val TYPE_LONG_LOWER_CASE = "long"

const val TYPE_DOUBLE = "Double"
const val TYPE_DOUBLE_LOWER_CASE = "double"

const val TYPE_FLOAT = "Float"
const val TYPE_FLOAT_LOWER_CASE = "float"

const val TYPE_BOOLEAN = "Boolean"
const val TYPE_BOOLEAN_LOWER_CASE = "boolean"

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

    @Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")
    fun <T> buildField(key: String): T? {
        val classField = getChannel()?.fields?.get(key) ?: return null
        return when (classField.type) {
            TYPE_STRING ->
                if (classField.value.startsWith("\"")) {
                    classField.value.substring(1 until classField.value.length - 1) as T
                } else {
                    classField.value as T
                }

            TYPE_INT, TYPE_INTEGER, TYPE_INT_LOWER_CASE -> classField.value.toIntOrNull() as T


            TYPE_LONG, TYPE_LONG_LOWER_CASE -> classField.value.toLongOrNull() as T


            TYPE_DOUBLE, TYPE_DOUBLE_LOWER_CASE -> classField.value.toDoubleOrNull() as T


            TYPE_FLOAT, TYPE_FLOAT_LOWER_CASE -> classField.value.toFloatOrNull() as T


            TYPE_BOOLEAN, TYPE_BOOLEAN_LOWER_CASE -> classField.value.toDoubleOrNull() as T

            else -> null

        }


    }

}


