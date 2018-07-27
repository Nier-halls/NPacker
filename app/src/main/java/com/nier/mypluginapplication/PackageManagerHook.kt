package com.nier.mypluginapplication

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import java.lang.reflect.Field
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.*

@SuppressLint("PrivateApi")
        /**
         * Created by Nier
         * Date 2018/7/19
         */
fun hook(): Any? {

    val activityThread = Class.forName("android.app.ActivityThread")
    val sPackageManager = activityThread.getDeclaredField("sPackageManager")
    sPackageManager.isAccessible = true

    val pmProxy = sPackageManager.get(null) ?: {
        val serviceManager = Class.forName("android.os.ServiceManager")
        val getServiceMethod = serviceManager.getDeclaredMethod("getService", *arrayOf(String::class.java))
        val pmBinderProxy = getServiceMethod.invoke(null, "package")
        val iPackageManagerStub = Class.forName("android.content.pm.IPackageManager\$Stub")
        val asInterfaceMethod = iPackageManagerStub.getDeclaredMethod("asInterface", *arrayOf(IBinder::class.java))
        asInterfaceMethod.isAccessible = true

        asInterfaceMethod.invoke(null, pmBinderProxy)
    }.invoke()


    val pmInterface = Class.forName("android.content.pm.IPackageManager")

    val getPackageInfoMethod = pmProxy::class.java.getDeclaredMethod("getPackageInfo", *arrayOf(String::class.java, Int::class.java, Int::class.java))

    val proxyInstance = Proxy.newProxyInstance(MainActivity::class.java.classLoader,
            arrayOf(pmInterface)
            , object : InvocationHandler {
        override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any? {
//            Log.d("fgd", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")

            if (method!!.declaringClass == Objects::class.java) {
                return method.invoke(this, *(args ?: emptyArray()))
            }

            if (method.name == "getApplicationInfo" &&
                    args != null &&
                    args.size == 3 &&
                    (args[0] is String) &&
                    (args[1] is Int) &&
                    (args[2] is Int)) {
                Log.e("fgd", "PackageManager->${method.name} with ${args
                        ?: "null array"} invoked.")
                val invoke = method.invoke(pmProxy, *(args ?: emptyArray()))
                if (invoke is ApplicationInfo) {
                    var metaField: Field? = try {
                        ApplicationInfo::class.java.getDeclaredField("metaData")
                    } catch (e: Exception) {
                        null
                    }
                    metaField ?: try {
                        metaField = ApplicationInfo::class.java.getField("metaData")
                    } catch (e: Exception) {
                        null
                    }
                    //这里为什么是NUll。。。
                    if (metaField != null) {
                        val metaBundle = metaField.get(invoke)
                        if (metaBundle is Bundle) {
                            metaBundle.putString("INJECT", "INJECT VALUE SUCCESS!!!")
                            return invoke
                        }
                    }
                }
                Log.w("fgd", "finish unexpected.")
                return invoke
            }


            var result: Any? = null
            try {
                result = method.invoke(pmProxy, *(args ?: emptyArray()))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            //            Log.d("fgd", "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
            return result
        }
    }
    )
    sPackageManager.set(null, proxyInstance)






    return true
}

//fun hookComtextImpl(context: Context): Boolean {
//    val mBaseField = context.javaClass.getDeclaredField("mBase")
//    mBaseField ?: return false
//    val mBaseValue = mBaseField.get(context)
//    mBaseValue ?: return false
//
//    Class.forName("ContextImpl")
//}

fun getPackageManagerProxy(): Any? {
    val activityThread = Class.forName("android.app.ActivityThread")
    val sPackageManager = activityThread.getDeclaredField("sPackageManager")
    sPackageManager.isAccessible = true
    return sPackageManager.get(null)
}