package com.android.smack.services

import android.content.Context
import android.util.Log
import com.android.smack.utilities.PARAM_EMAIL
import com.android.smack.utilities.PARAM_PASSWORD
import com.android.smack.utilities.URL_REGISTER
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

object AuthService {


    fun registerUser(
        context: Context,
        email: String,
        password: String,
        complete: (Boolean) -> Unit
    ) {

        val jsonBody = JSONObject()
        jsonBody.put(PARAM_EMAIL, email)
        jsonBody.put(PARAM_PASSWORD, password)
        val requestBody = jsonBody.toString()

        val registerRequest =
            object : StringRequest(Method.POST, URL_REGISTER, Response.Listener { response ->
                Log.d("SUCCESS", response)
                complete(true)
            }, Response.ErrorListener { error ->
                Log.d("ERROR", "could not register user $error")
                complete(false)
            }) {
                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }

                override fun getBody(): ByteArray {
                    return requestBody.toByteArray()
                }
            }
        Volley.newRequestQueue(context).add(registerRequest)
    }
}