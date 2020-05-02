package com.android.smack.utilities

import android.content.Context
import android.content.SharedPreferences
import com.android.volley.toolbox.Volley

class SharedPrefs(private val context:Context) {

   val prefs:SharedPreferences = context.getSharedPreferences(SHARED_PREF_FILENAME,0)

    var isLoggedIn: Boolean
        get() = prefs.getBoolean(SHARED_IS_LOGGED_IN, false)
        set(value) = prefs.edit().putBoolean(SHARED_IS_LOGGED_IN, value).apply()


    var authToken: String?
        get() = prefs.getString(SHARED_AUTH_TOKEN,"")
        set(value) = prefs.edit().putString(SHARED_AUTH_TOKEN,value).apply()

    var userEmail: String?
        get() = prefs.getString(SHARED_USER_EMAIL,"")
        set(value) = prefs.edit().putString(SHARED_USER_EMAIL,value).apply()

    val requestQueue = Volley.newRequestQueue(context)
}