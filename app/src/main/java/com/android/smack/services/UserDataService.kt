package com.android.smack.services

import android.graphics.Color
import com.android.smack.controller.SmackApplication
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
        SmackApplication.prefs.authToken = ""
        SmackApplication.prefs.userEmail = ""
        SmackApplication.prefs.isLoggedIn = false

        MessageService.clearMessages()
        MessageService.clearChannels()
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