package com.android.smack.controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.smack.R
import com.android.smack.adapter.MessageRecyclerViewAdapter
import com.android.smack.model.Channel
import com.android.smack.model.Message
import com.android.smack.services.AuthService
import com.android.smack.services.MessageService
import com.android.smack.services.UserDataService
import com.android.smack.utilities.*
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    private val socket = IO.socket(SOCKET_URL)
    lateinit var channelAdapter: ArrayAdapter<Channel>
    lateinit var messageAdapter: MessageRecyclerViewAdapter
    var selectedChannel:Channel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        socket.connect()
        // socket is listening for incoming channel event
        socket.on(CHANNEL_CREATED_EVENT, onNewChannel)
        socket.on(MESSAGE_CREATED_EVENT, onNewMessage)

        val toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        setupAdapters()

        LocalBroadcastManager.getInstance(this).registerReceiver(
            userDataChangeReceiver,
            IntentFilter(BROADCAST_USER_DATA_CHANGE)
        )

        channel_list.setOnItemClickListener { _, _, position, _ ->
            selectedChannel = MessageService.channels[position]
            drawer_layout.closeDrawer(GravityCompat.START)
            updateChannelText()
        }

        if (SmackApplication.prefs.isLoggedIn) {
            AuthService.findUserByEmail(this) {}
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        socket.disconnect()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataChangeReceiver)
    }

    private val userDataChangeReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (SmackApplication.prefs.isLoggedIn) {
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

                MessageService.getChannels { success ->
                    if (success) {
                        if(MessageService.channels.count() > 0){
                            selectedChannel = MessageService.channels[0]
                            channelAdapter.notifyDataSetChanged()
                            updateChannelText()
                        }

                    }
                }
            }
        }
    }

    fun updateChannelText(){
        mainChannelName.text = "#${selectedChannel?.name}"

        if(selectedChannel!=null){
            MessageService.getMessages(selectedChannel!!.id){complete->
                if(complete){
                   updateMessageRecyclerview()
                }
            }
        }

    }

    private fun setupAdapters() {
        channelAdapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, MessageService.channels)
        channel_list.adapter = channelAdapter

        messageAdapter = MessageRecyclerViewAdapter(this,MessageService.messages)
        messageRecyclerView.adapter = messageAdapter
        messageRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun loginBtnNavHeaderClicked(view: View) {
        if (SmackApplication.prefs.isLoggedIn) {
            UserDataService.logout()
            channelAdapter.notifyDataSetChanged()
            messageAdapter.notifyDataSetChanged()
            userNameNavHeader.text = ""
            userEmailNavHeader.text = ""
            userImageNavHeader.setBackgroundColor(Color.TRANSPARENT)
            userImageNavHeader.setImageResource(R.drawable.profiledefault)
            loginBtnNavHeader.text = getString(R.string.login_button_text)
            mainChannelName.text = getString(R.string.please_login_text)
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    fun addChannelButtonClicked(view: View) {
        if (SmackApplication.prefs.isLoggedIn) {
            showChannelSelectionDialog()
        }
    }

    fun sendMessageButtonClicked(view: View) {
        if(SmackApplication.prefs.isLoggedIn && messageTextField.text.isNotEmpty() && selectedChannel!=null){
            // Send a new message to API
            socket.emit(MESSAGE_EVENT,messageTextField.text.toString(),UserDataService.id,selectedChannel!!.id,
                UserDataService.name,UserDataService.avatarName,UserDataService.avatarColor)
            messageTextField.text.clear()
            hideKeyboard()
        }
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
        if(SmackApplication.prefs.isLoggedIn){
            // to update the UI with new Channel details we have to use runOnUiThread function
            runOnUiThread {
                val newChannel = Channel(args[0] as String, args[1] as String, args[2] as String)
                MessageService.channels.add(newChannel)
                channelAdapter.notifyDataSetChanged()
            }
        }
    }

    private val onNewMessage = Emitter.Listener {args ->
        if(SmackApplication.prefs.isLoggedIn){
            runOnUiThread{
                val channelId = args[2] as String
                if(channelId == selectedChannel?.id){
                    val messageBody = args[0] as String
                    val userName = args[3] as String
                    val avatar = args[4] as String
                    val avatarColor = args[5] as String
                    val messageId = args[6] as String
                    val messageTime = args[7] as String

                    val newMessage = Message(messageBody,channelId,userName,avatar,avatarColor,messageId,messageTime)
                    MessageService.messages.add(newMessage)
                    updateMessageRecyclerview()
                }

            }
        }
    }

    private fun updateMessageRecyclerview(){
        messageAdapter.notifyDataSetChanged()
        if(messageAdapter.itemCount > 0){
            messageRecyclerView.scrollToPosition(messageAdapter.itemCount - 1)
        }
    }
}
