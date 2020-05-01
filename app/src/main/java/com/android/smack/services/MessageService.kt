package com.android.smack.services

import android.content.Context
import android.util.Log
import com.android.smack.model.Channel
import com.android.smack.utilities.*
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException

object MessageService {

    val channels = ArrayList<Channel>()

    fun getChannels(context: Context,complete: (Boolean)-> Unit){

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
                headers.put(PARAM_AUTHORIZATION, "Bearer ${AuthService.authToken}")
                return headers
            }
        }

        Volley.newRequestQueue(context).add(getChannelRequest)
    }
}