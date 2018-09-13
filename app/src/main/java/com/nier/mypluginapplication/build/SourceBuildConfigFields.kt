package com.nier.mypluginapplication.build

import android.content.Context
import com.nier.mypluginapplication.MyApp

/**
 * Created by fgd
 * Date 2018/9/11
 */
const val BUILD_CONFIG_CLASS_NAME = "BuildConfig"

class SourceBuildConfigFields private constructor() : IBuildConfigFields {

    companion object {
        private val mInstance = SourceBuildConfigFields()
        fun getInstance(): SourceBuildConfigFields = mInstance
    }


    override fun <T> getField(key: String): T? {
        return try {
            val packageName = MyApp.getApplication()?.packageName
            if (packageName.isNullOrEmpty()) return null
            val buildConfigClass = Class.forName("$packageName.$BUILD_CONFIG_CLASS_NAME")
            buildConfigClass.getField(key).get(null) as T
        } catch (e: Exception) {
            return null
        }
    }

//    private fun getPackageName(): String? {
//        return try {
//            val packageManager = MyApp.getApplication()?.packageName
//            val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
//            packageInfo.applicationInfo.packageName
//        } catch (e: Exception) {
//            e.printStackTrace()
//            return null
//        }
//    }

}