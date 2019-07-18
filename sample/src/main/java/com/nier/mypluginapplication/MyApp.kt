package com.nier.mypluginapplication

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MyApp : Application() {


    override fun onCreate() {
        super.onCreate()
        hook()

        mSelf = this
    }

    companion object {
        private lateinit var mSelf: Application
        fun getApplication(): Application = mSelf
    }


}
