package com.android.smack.controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.smack.R
import com.android.smack.model.Channel
import com.android.smack.services.AuthService
import com.android.smack.services.MessageService
import com.android.smack.services.UserDataService
import com.android.smack.utilities.*
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    private val socket = IO.socket(SOCKET_URL)
    lateinit var channelAdapter: ArrayAdapter<Channel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        socket.connect()
        // socket is listening for incoming channel event
        socket.on(CHANNEL_CREATED_EVENT, onNewChannel)

        val toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        setupChannelAdapter()
    }

    override fun onResume() {
        super.onResume()

        LocalBroadcastManager.getInstance(this).registerReceiver(
            userDataChangeReceiver,
            IntentFilter(BROADCAST_USER_DATA_CHANGE)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        socket.disconnect()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataChangeReceiver)
    }

    private val userDataChangeReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (AuthService.isLoggedIn) {
                userNameNavHeader.text = UserDataService.name
                userEmailNavHeader.text = UserDataService.email
                val resourceId = resources.getIdentifier(
                    UserDataService.avatarName, DRAWABLE,
                    packageName
                )
                userImageNavHeader.setImageResource(resourceId)
                userImageNavHeader.setBackgroundColor(
                    UserDataService.returnAvatarColor(
                        UserDataService.avatarColor
                    )
                )
                loginBtnNavHeader.text = getString(R.string.logout_button_text)

                MessageService.getChannels(context) { success ->
                    if (success) {
                        channelAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

    }

    private fun setupChannelAdapter() {
        channelAdapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, MessageService.channels)
        channel_list.adapter = channelAdapter
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun loginBtnNavHeaderClicked(view: View) {
        if (AuthService.isLoggedIn) {
            UserDataService.logout()
            userNameNavHeader.text = ""
            userEmailNavHeader.text = ""
            userImageNavHeader.setBackgroundColor(Color.TRANSPARENT)
            userImageNavHeader.setImageResource(R.drawable.profiledefault)
            loginBtnNavHeader.text = getString(R.string.login_button_text)

        } else {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    fun addChannelButtonClicked(view: View) {
        if (AuthService.isLoggedIn) {
            showChannelSelectionDialog()
        }
    }

    fun sendMessageButtonClicked(view: View) {
        hideKeyboard()
    }

    private fun showChannelSelectionDialog() {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)

        builder.setView(dialogView)
            .setPositiveButton(getString(R.string.add_button_text)) { _, _ ->
                val channelName =
                    dialogView.findViewById<EditText>(R.id.addChannelNameText).text.toString()
                val channelDesc = dialogView.findViewById<EditText>(R.id.addChannelDescriptionText)
                    .text.toString()

                socket.emit(CHANNEL_EVENT, channelName, channelDesc)
            }
            .setNegativeButton(getString(R.string.cancel_button_text)) { _, _ ->
            }.show()
    }

    private fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }

    private val onNewChannel = Emitter.Listener { args -> // This is worker or background thread

        // to update the UI with new Channel details we have to use runOnUiThread function
        runOnUiThread {
            val newChannel = Channel(args[0] as String, args[1] as String, args[2] as String)
            MessageService.channels.add(newChannel)
            channelAdapter.notifyDataSetChanged()
        }
    }
}
