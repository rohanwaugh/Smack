package com.android.smack.controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.android.smack.R

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }


    fun signUpButtonClicked(view: View) {
        val createUserIntent = Intent(this,
            CreateUserActivity::class.java)
        startActivity(createUserIntent)
        finish()
    }

    fun loginButtonClicked(view: View) {

    }
}
