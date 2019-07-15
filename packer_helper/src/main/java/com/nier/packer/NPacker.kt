package com.nier.packer

import com.nier.packer.channel.Channel
import com.nier.packer.support.Apk
import com.nier.packer.support.IExtraPayloadData
import java.io.File

/**
 * Created by fgd
 * Date 2018/9/5
 */


private const val TYPE_STRING = "String"

private const val TYPE_INT = "Int"
private const val TYPE_INT_LOWER_CASE = "int"
private const val TYPE_INTEGER = "Integer"

private const val TYPE_LONG = "Long"
private const val TYPE_LONG_LOWER_CASE = "long"

private const val TYPE_DOUBLE = "Double"
private const val TYPE_DOUBLE_LOWER_CASE = "double"

private const val TYPE_FLOAT = "Float"
private const val TYPE_FLOAT_LOWER_CASE = "float"

private const val TYPE_BOOLEAN = "Boolean"
private const val TYPE_BOOLEAN_LOWER_CASE = "boolean"

class NPacker private constructor() {

    private lateinit var apk: Apk
    private val LOCK = Object()
    private var packageName: String? = null
    private val fieldCache = HashMap<String, Any>()

    companion object {
        fun init(apkFile: File, packageName: String? = null): NPacker {
            val packer = NPacker()
            packer.apk = Apk.createApk(apkFile)
            packer.packageName = packageName
            return packer
        }
    }

    fun setPackageName(packageName: String?) {
        this.packageName = packageName
    }

    fun injectData(data: IExtraPayloadData) {
        apk.injectExtraData(data)
    }

    private var currentChannel: Channel? = null
        get() {
            if (field == null) {
                synchronized(LOCK) {
                    try {
                        val rawChannelData = apk.getExtraData(DEFAULT_EXTRA_PAYLOAD_KEY)
                        field = if (rawChannelData != null) {
                            Channel.parse(rawChannelData)
                        } else {
                            Channel.NULL
                        }
                        if (field == null) {
                            field = Channel.NULL
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            return field
        }


    /**
     * 获取渠道名
     */
    fun channelName(): String? = currentChannel?.name

    /**
     * 获取渠道号
     */
    fun channelCode(): String? = currentChannel?.key


    /**
     * 查找对应的渠道信息
     */
    @Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")
    fun <T> findBuildField(key: String): T? {
        //尝试从缓存中去获取一次数据
        val cache = fieldCache[key]
        if (cache != null) return cache as T

        //尝试从APK中获取一次
        val fieldFromSignBlock = findBuildFieldFromApkSignBlock<T>(key)
        if (fieldFromSignBlock != null) {
            fieldCache[key] = fieldFromSignBlock
            return fieldFromSignBlock
        }

        //尝试从默认的BuildField文件中获取一次
        val defaultAndroidField = findBuildFieldDefault<T>(key)
        if (defaultAndroidField != null) {
            fieldCache[key] = defaultAndroidField
            return defaultAndroidField
        }

        return null
    }

    /**
     * 解析APK中的sign block，从里面获取渠道信息
     */
    private fun <T> findBuildFieldFromApkSignBlock(key: String): T? {
        //如果是interrupt的情况nio读取数据时会崩溃，因此记录等读取完成后恢复
        val isThreadInterrupted = Thread.interrupted()
        try {
            if (currentChannel == null || !currentChannel!!.isValid()) {
                return null
            }
            val classField = currentChannel?.fields?.get(key) ?: return null
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


                TYPE_BOOLEAN, TYPE_BOOLEAN_LOWER_CASE -> classField.value.toBoolean() as T

                else -> null
            }
        } finally {
            if (isThreadInterrupted) {
                Thread.currentThread().interrupt()
            }
        }
    }

    /**
     * 反射获取BuildField渠道信息
     */
    private fun <T> findBuildFieldDefault(key: String): T? {
        try {
            if (packageName.isNullOrEmpty()) return null
            val buildConfigClass = Class.forName("$packageName.$BUILD_CONFIG_CLASS_NAME")
            return buildConfigClass.getField(key).get(null) as T
        } catch (e: Exception) {
            return null
        }
    }

}


