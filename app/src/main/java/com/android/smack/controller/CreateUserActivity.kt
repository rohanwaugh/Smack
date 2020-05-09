package com.android.smack.controller

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.smack.R
import com.android.smack.services.AuthService
import com.android.smack.utilities.BROADCAST_USER_DATA_CHANGE
import com.android.smack.utilities.hide
import com.android.smack.utilities.show
import kotlinx.android.synthetic.main.activity_create_user.*
import java.util.*

class CreateUserActivity : AppCompatActivity() {

    var userAvatar = "profileDefault"
    var avatarColor = "[0.5, 0.5, 0.5, 1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
        createSpinner.hide()
    }


    fun generateBackgroundColorButtonClicked(view: View) {
        val random = Random()
        val r = random.nextInt(255)
        val g = random.nextInt(255)
        val b = random.nextInt(255)

        createAvatarImage.setBackgroundColor(Color.rgb(r, g, b))

        val savedR = r.toDouble() / 255
        val savedG = g.toDouble() / 255
        val savedB = b.toDouble() / 255

        avatarColor = "[$savedR, $savedG, $savedB , 1]"
        println(avatarColor)
    }


    fun generateUserAvatar(view: View) {
        val random = Random()
        val color = random.nextInt(2)
        val avatar = random.nextInt(28)

        userAvatar = if (color == 0) {
            "light$avatar"
        } else {
            "dark$avatar"
        }

        val resourceId = resources.getIdentifier(userAvatar, "drawable", packageName)
        createAvatarImage.setImageResource(resourceId)
    }

    fun createUserButtonClicked(view: View) {
        enableSpinner(true)
        val userName = createUsernameText.text.toString()
        val email = createEmailText.text.toString()
        val password = createPasswordText.text.toString()

        if (userName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
            AuthService.registerUser(email, password) { registerSuccess ->
                if (registerSuccess) {
                    Log.d("XYZ", "Register Success")
                    AuthService.loginUser(email, password) { loginSuccess ->
                        if (loginSuccess) {
                            AuthService.createUser(
                                userName,
                                email,
                                userAvatar,
                                avatarColor
                            ) { createSuccess ->
                                if (createSuccess) {
                                    val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                                    LocalBroadcastManager.getInstance(this)
                                        .sendBroadcast(userDataChange)
                                    enableSpinner(false)
                                    finish()
                                } else {
                                    errorToast(getString(R.string.create_user_error_text))
                                }
                            }
                        } else {
                            errorToast(getString(R.string.login_error_text))
                        }
                    }
                } else {
                    errorToast(getString(R.string.registration_error_text))
                }
            }
        } else {
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
            createSpinner.show()
            createSpinner.show()
        } else {
            createSpinner.hide()
        }
        createUserButton.isEnabled = !isEnable
        createAvatarImage.isEnabled = !isEnable
        generateBackgroundColorButton.isEnabled = !isEnable
    }
}
