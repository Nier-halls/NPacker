package com.nier.mypluginapplication.build

import android.content.pm.PackageManager
import android.util.Log
import com.nier.mypluginapplication.BuildConfig
import com.nier.mypluginapplication.MyApp
import com.nier.packer.NPacker
import java.io.File
import java.io.IOException
import java.util.jar.Pack200

/**
 * Created by fgd
 * Date 2018/9/11
 */
class ChannelBuildConfigFields private constructor() : IBuildConfigFields {

    private val packer: NPacker

    init {
        packer = NPacker.init(getApkFile())
    }

    companion object {
        private val mInstance = ChannelBuildConfigFields()
        fun getInstance(): ChannelBuildConfigFields = mInstance
    }


    override fun <T> getField(key: String): T? {
        return packer.findBuildField<T>(key)
    }


    private fun getApkFile(): File {
        val pm = MyApp.getApplication().packageManager
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        for (app in apps) {
            if (app.packageName.startsWith(MyApp.getApplication().packageName)) {
                return File(app.sourceDir)
            }
        }
        throw IOException("can not find current package name (${MyApp.getApplication().packageName}) apk file")
    }

}