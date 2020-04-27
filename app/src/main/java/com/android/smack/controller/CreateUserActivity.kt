package com.android.smack.controller

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.android.smack.R
import com.android.smack.services.AuthService
import kotlinx.android.synthetic.main.activity_create_user.*
import java.util.*

class CreateUserActivity : AppCompatActivity() {

    var userAvatar = "profileDefault"
    var avatarColor = "[0.5, 0.5, 0.5, 1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
    }


    fun generateBackgroundColorButtonClicked(view: View) {
        val random = Random()
        val r = random.nextInt(255)
        val g = random.nextInt(255)
        val b = random.nextInt(255)

        createAvatarImage.setBackgroundColor(Color.rgb(r,g,b))

        val savedR = r.toDouble() / 255
        val savedG = g.toDouble() / 255
        val savedB = b.toDouble() / 255

        avatarColor = "[$savedR, $savedG, $savedB , 1]"
        println(avatarColor)
    }

    fun createUserButtonClicked(view: View){
        val email = createEmailText.text.toString()
        val password = createPasswordText.text.toString()
        AuthService.registerUser(this,email,password){ registerSuccess->
            if(registerSuccess){
                Log.d("XYZ","Register Success")
                AuthService.loginUser(this,email,password){ loginSuccess->
                    if(loginSuccess){
                        Log.d("XYZ","Login Success")
                        Log.d("XYZ",AuthService.authToken)
                        Log.d("XYZ",AuthService.userEmail)
                    }
                }
            }
        }
    }

    fun generateUserAvatar(view: View){
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
}
