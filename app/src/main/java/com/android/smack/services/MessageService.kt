package com.android.smack.services

import android.content.Context
import android.util.Log
import com.android.smack.controller.SmackApplication
import com.android.smack.model.Channel
import com.android.smack.model.Message
import com.android.smack.utilities.*
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException

object MessageService {

    val channels = ArrayList<Channel>()
    val messages = ArrayList<Message>()

    fun getChannels(complete: (Boolean)-> Unit){

        val getChannelRequest = object : JsonArrayRequest(Method.GET, URL_GET_CHANNEL,null,Response.Listener {response->

            try {
                for( count in 0 until response.length()){
                    val channel = response.getJSONObject(count)
                    val newChannel = Channel(channel.getString(CHANNEL_NAME),channel.getString(CHANNEL_DESCRIPTION),channel.getString(CHANNEL_ID))
                    this.channels.add(newChannel)
                }
                complete(true)
            }catch (e: JSONException){
                Log.d("JSON", e.localizedMessage)
                complete(false)
            }
        },Response.ErrorListener {error->
            Log.d("GET CHANNEL ERROR", "could not get channels $error")
            complete(false)
        }){
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put(PARAM_AUTHORIZATION, "Bearer ${SmackApplication.prefs.authToken}")
                return headers
            }
        }

        SmackApplication.prefs.requestQueue.add(getChannelRequest)
    }
    
    fun getMessages(channelId:String,complete: (Boolean) -> Unit){

        val url = "$URL_GET_MESSAGES$channelId"
        val getMessageRequest = object : JsonArrayRequest(Method.GET, url,null,Response.Listener {response ->
            clearMessages()
            try{
                for(count in 0 until response.length()){
                    val message = response.getJSONObject(count)
                    val newMessage = Message(message.getString(PARAM_MESSAGE_BODY),message.getString(PARAM_CHANNEL_ID),
                        message.getString(PARAM_USER_NAME),message.getString(PARAM_USER_AVATAR),message.getString(PARAM_USER_AVATAR_COLOR),
                        message.getString(PARAM_MESSAGE_ID),message.getString(PARAM_MESSAGE_TIMESTAMP))
                    this.messages.add(newMessage)
                }
                complete(true)
            }catch(e :JSONException){
                Log.d("JSON", e.localizedMessage)
                complete(false)
            }
        },Response.ErrorListener {error->
            Log.d("GET MESSAGE ERROR", "could not get msessages $error")
            complete(false)
        }){
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put(PARAM_AUTHORIZATION, "Bearer ${SmackApplication.prefs.authToken}")
                return headers
            }
        }
        SmackApplication.prefs.requestQueue.add(getMessageRequest)
    }

    fun clearMessages() = messages.clear()

    fun clearChannels() = channels.clear()
}