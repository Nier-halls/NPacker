package com.nier.mypluginapplication

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import com.nier.packer.NPacker
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException


/**
 * Created by Nier
 * Date 2018/7/19
 */

class MainActivity : AppCompatActivity() {
    private val npacker = NPacker.init(getApkFile(), MyApp.getApplication().packageName)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        packer()
    }

    private fun packer() {


        val result = "channelName = ${npacker.channelName()} \n" +
                "channelCode = ${npacker.channelCode()} \n" +
                "field1 = ${npacker.findBuildField<String>("field1")} \n" +
                "field2 = ${npacker.findBuildField<String>("field2")} \n" +
                "field3 = ${npacker.findBuildField<String>("field3")} \n" +
                "buildTypeField = ${npacker.findBuildField<String>("buildTypeField")} \n"

        tvResult.text = result
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