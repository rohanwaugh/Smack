package com.android.smack.services

import android.graphics.Color
import java.util.*

object UserDataService {
    var name = ""
    var email =  ""
    var avatarName = ""
    var avatarColor = ""
    var id = ""

    fun logout() {
        name = ""
        email = ""
        avatarName = ""
        avatarColor = ""
        id = ""
        AuthService.authToken = ""
        AuthService.userEmail = ""
        AuthService.isLoggedIn = false
    }

    fun returnAvatarColor(components: String): Int {
        //[0.6392156862745098, 0.47843137254901963, 0.09411764705882353 , 1]
        val strippedColor = components.replace("[", "")
            .replace("]", "")
            .replace(",", "")

        var r = 0
        var g = 0
        var b = 0

        val scanner = Scanner(strippedColor)
        if (scanner.hasNext()) {
            r = (scanner.nextDouble() * 255).toInt()
            g = (scanner.nextDouble() * 255).toInt()
            b = (scanner.nextDouble() * 255).toInt()
        }

        return Color.rgb(r, g, b)
    }
}