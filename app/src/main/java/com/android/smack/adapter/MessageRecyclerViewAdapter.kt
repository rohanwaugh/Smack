package com.android.smack.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.smack.R
import com.android.smack.model.Message
import com.android.smack.services.UserDataService
import com.android.smack.utilities.DRAWABLE
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MessageRecyclerViewAdapter(val context: Context, val messageList: ArrayList<Message>) :
    RecyclerView.Adapter<MessageRecyclerViewAdapter.MessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.message_list_view, parent, false)
        return MessageViewHolder(view)
    }

    override fun getItemCount(): Int = messageList.count()

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bindMessage(context, messageList[position])
    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val userImage = itemView.findViewById<ImageView>(R.id.messageUserImage)
        private val timeStamp = itemView.findViewById<TextView>(R.id.messageTimeStamp)
        private val userName = itemView.findViewById<TextView>(R.id.messageUserNameText)
        private val messageBody = itemView.findViewById<TextView>(R.id.messageBody)

        fun bindMessage(context: Context, message: Message) {
            userName?.text = message.userName
            timeStamp?.text = returnDateString(message.messageTime)
            messageBody?.text = message.message

            val resourceId =
                context.resources.getIdentifier(message.avatar, DRAWABLE, context.packageName)
            userImage?.setImageResource(resourceId)
            userImage?.setBackgroundColor(UserDataService.returnAvatarColor(message.avatarColor))
        }

        private fun returnDateString(isoString: String): String {
            // 2020-05-02T10:56:39.024Z
            // Monday 4:35 PM
            val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            isoFormatter.timeZone = TimeZone.getTimeZone("UTC")

            var convertedDate = Date()
            try {
                convertedDate = isoFormatter.parse(isoString)
            } catch (e: ParseException) {
                Log.d("PARSE", e.localizedMessage)
            }

            val outDateString = SimpleDateFormat("E, h:mm a", Locale.getDefault())
            return outDateString.format(convertedDate)
        }
    }
}