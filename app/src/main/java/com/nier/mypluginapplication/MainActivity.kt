package com.nier.mypluginapplication

//import com.nier.inject.Apk
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.nier.packer.Packer
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
//        verifyStoragePermissions(this)
        test()

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1) {
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "" + "权限" + permissions[i] + "申请成功", Toast.LENGTH_SHORT).show()
                    test()
                } else {
                    Toast.makeText(this, "" + "权限" + permissions[i] + "申请失败", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun test() {
        val pm = packageManager
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        for (app in apps) {
            if (app.packageName.startsWith("com.nier.mypluginapplication")) {
                val apk = Packer.init(File(app.sourceDir))
                Log.d("fgd", "app version = ${BuildConfig.VERSION_NAME}_${BuildConfig.VERSION_CODE}")
                Log.d("fgd", "app channel = ${apk.channelName()}")
                Log.d("fgd", "app channel code = ${apk.channelCode()}")
                Log.d("fgd", "app channel field1 = ${apk.extraData("field1")}")
                Log.d("fgd", "app channel field2 = ${apk.extraData("field2")}")
                Log.d("fgd", "app channel field3 = ${apk.extraData("field3")}")

                Log.d("fgd", "BuildConfig.testString >>> ${BuildConfig.testString}")
//                RandomAccessFile(File(app.sourceDir), "r").use {
//                    it.channel.use {
//                        print("")
//                    }
//                }
//                RandomAccessFile(File(app.sourceDir), "rw").use {
//                    it.channel.use {
//                        print("")
//                    }
//                }


//                Log.d("TAG", "app=" + app.packageName + ", channel="
//                        + read())
            }
        }
    }

    // Storage Permissions
    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    fun verifyStoragePermissions(activity: Activity) {
        // Check if we have write permission
        val permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            )
        } else {
            test()
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