package com.android.smack.controller

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.android.smack.R
import com.android.smack.services.AuthService
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginSpinner.visibility = View.INVISIBLE
    }


    fun signUpButtonClicked(view: View) {
        val createUserIntent = Intent(this,
            CreateUserActivity::class.java)
        startActivity(createUserIntent)
        finish()
    }

    fun loginButtonClicked(view: View) {
        enableSpinner(true)
        val email = loginEmailText.text.toString()
        val password = loginPasswordText.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            hideKeyboard()
            AuthService.loginUser(this, email, password) { loginSuccess ->
                if (loginSuccess) {
                    AuthService.findUserByEmail(this) { findSuccess ->
                        if (findSuccess) {
                            enableSpinner(false)
                            finish()
                        }else{
                            errorToast(getString(R.string.user_not_found_text))
                        }
                    }
                }else{
                    errorToast(getString(R.string.login_error_text))
                }
            }
        }else{
            errorToast(getString(R.string.user_validation_text))
            enableSpinner(false)
        }
    }

    private fun errorToast(toastMessage: String) {
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()
        enableSpinner(false)
    }

    private fun enableSpinner(isEnable: Boolean) {
        if (isEnable) {
            loginSpinner.visibility = View.VISIBLE
        } else {
            loginSpinner.visibility = View.INVISIBLE
        }
        loginButton.isEnabled = !isEnable
        signUpButton.isEnabled = !isEnable
    }

    private fun hideKeyboard(){
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if(inputManager.isAcceptingText){
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken,0)
        }
    }
}
