package com.nier.mypluginapplication.build

import android.content.Context

/**
 * Created by fgd
 * Date 2018/9/11
 */


interface IBuildConfigFields {
    fun <T> getField(key: String): T?
}

class AppBuildConfig : IBuildConfigFields {
    override fun <T> getField(key: String): T? {
        return try {
            ChannelBuildConfigFields.getInstance().getField<T>(key)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } ?: try {
            SourceBuildConfigFields.getInstance().getField<T>(key)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    companion object {
        private val mInstance = AppBuildConfig()
        fun getInstance(): AppBuildConfig = mInstance
    }

}


