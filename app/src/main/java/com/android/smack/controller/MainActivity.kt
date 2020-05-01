package com.android.smack.controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.smack.R
import com.android.smack.services.AuthService
import com.android.smack.services.UserDataService
import com.android.smack.utilities.BROADCAST_USER_DATA_CHANGE
import com.android.smack.utilities.DRAWABLE
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver,
            IntentFilter(BROADCAST_USER_DATA_CHANGE)
        )

    }

    private val userDataChangeReceiver = object: BroadcastReceiver(){

        override fun onReceive(context: Context?, intent: Intent) {
            if(AuthService.isLoggedIn){
                userNameNavHeader.text = UserDataService.name
                userEmailNavHeader.text = UserDataService.email
                val resourceId = resources.getIdentifier(UserDataService.avatarName,DRAWABLE,
                    packageName)
                userImageNavHeader.setImageResource(resourceId)
                userImageNavHeader.setBackgroundColor(UserDataService.returnAvatarColor(UserDataService.avatarColor))
                loginBtnNavHeader.text = getString(R.string.logout_button_text)
            }
        }

    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun loginBtnNavHeaderClicked(view: View) {
        if(AuthService.isLoggedIn){
            UserDataService.logout()
            userNameNavHeader.text = ""
            userEmailNavHeader.text = ""
            userImageNavHeader.setBackgroundColor(Color.TRANSPARENT)
            userImageNavHeader.setImageResource(R.drawable.profiledefault)
            loginBtnNavHeader.text = getString(R.string.login_button_text)

        }else{
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    fun addUserButtonClicked(view: View) {

    }

    fun sendMessageButtonClicked(view : View){

    }
}
