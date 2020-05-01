package com.android.smack.controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.android.smack.R
import com.android.smack.services.AuthService
import kotlinx.android.synthetic.main.activity_login.*

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
        val email = loginEmailText.text.toString()
        val password = loginPasswordText.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            AuthService.loginUser(this, email, password) { loginSuccess ->
                if (loginSuccess) {
                    AuthService.findUserByEmail(this) { findSuccess ->
                        if (findSuccess) {
                            finish()
                        }
                    }
                }
            }
        }
    }
}
