package com.nier.mypluginapplication

import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.nier.inject.findApkEOCDSignature

/**
 * Created by Nier
 * Date 2018/7/19
 */

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        hook()
        var applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        var metaData = applicationInfo.metaData
        if (metaData != null) {
            Log.d("fgd", "try get TEST meta ->${metaData.get("INJECTOR")}")
        }

        Log.d("fgd", ">>>>>>>> ActivityThread -> sPackageManager = ${getPackageManagerProxy()}")

        findApkEOCDSignature()
    }
}