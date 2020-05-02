package com.android.smack.controller

import android.app.Application
import com.android.smack.utilities.SharedPrefs

class SmackApplication : Application(){

    companion object{
        lateinit var prefs: SharedPrefs
    }

    override fun onCreate() {
        super.onCreate()
        prefs = SharedPrefs(applicationContext)
    }
}