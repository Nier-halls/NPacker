package com.nier.mypluginapplication

import android.app.Application
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        hook()
    }
}
