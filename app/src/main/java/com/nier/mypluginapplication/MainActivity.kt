package com.nier.mypluginapplication

import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.content.pm.ApplicationInfo
import com.nier.inject.Apk
import com.nier.inject.readPayloadById
import java.io.File
import java.nio.ByteBuffer


/**
 * Created by Nier
 * Date 2018/7/19
 */

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        hook()
//        var applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
//        var metaData = applicationInfo.metaData
//        if (metaData != null) {
//            Log.d("fgd", "try get TEST meta ->${metaData.get("INJECTOR")}")
//        }
//
//        Log.d("fgd", ">>>>>>>> ActivityThread -> sPackageManager = ${getPackageManagerProxy()}")
        test()
    }


    fun test() {
        val pm = packageManager
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        for (app in apps) {
            if (app.packageName.startsWith("com.nier.mypluginapplication")) {
                val apk = Apk.createApk(File(app.sourceDir))
              Log.d("fgd","extrad data = ${read(readPayloadById(apk, 1541))}")

//                Log.d("TAG", "app=" + app.packageName + ", channel="
//                        + read())
            }
        }
    }

}


fun read(byteBuffer: ByteBuffer?): String {
    if (byteBuffer == null) {
        Log.d("fgd", "Nullllllllllllllllllll")
        return "null"
    }
    val len = byteBuffer.limit()
    val byte: ByteArray = ByteArray(len)
    byteBuffer.get(byte)
    return String(byte)
}