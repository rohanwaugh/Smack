package com.android.smack.services

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.smack.utilities.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

object AuthService {

    var isLoggedIn = false
    var userEmail = ""
    var authToken = ""

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
                Log.d("REGISTER SUCCESS", response)
                complete(true)
            }, Response.ErrorListener { error ->
                Log.d("REGISTER ERROR", "could not register user $error")
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

    fun loginUser(context: Context, email: String, password: String, complete: (Boolean) -> Unit) {

        val jsonBody = JSONObject()
        jsonBody.put(PARAM_EMAIL, email)
        jsonBody.put(PARAM_PASSWORD, password)
        val requestBody = jsonBody.toString()

        val loginRequest =
            object : JsonObjectRequest(Method.POST, URL_LOGIN, null, Response.Listener { response ->
                Log.d("LOGIN SUCCESS", response.toString())
                try {
                    userEmail = response.getString(PARAM_USER)
                    authToken = response.getString(PARAM_TOKEN)
                    isLoggedIn = true
                    complete(true)
                } catch (e: JSONException) {
                    Log.d("JSON", e.localizedMessage)
                    complete(false)
                }

            }, Response.ErrorListener { error ->
                Log.d("LOGIN ERROR", "could not login user $error")
                complete(false)
            }) {
                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }

                override fun getBody(): ByteArray {
                    return requestBody.toByteArray()
                }
            }
        Volley.newRequestQueue(context).add(loginRequest)
    }

    fun createUser(
        context: Context,
        name: String,
        email: String,
        avatarName: String,
        avatarColor: String,
        complete: (Boolean) -> Unit
    ) {

        val jsonBody = JSONObject()
        jsonBody.put(PARAM_NAME, name)
        jsonBody.put(PARAM_EMAIL, email)
        jsonBody.put(PARAM_AVATAR_NAME, avatarName)
        jsonBody.put(PARAM_AVATAR_COLOR, avatarColor)
        val requestBody = jsonBody.toString()

        val createUserRequest = object :
            JsonObjectRequest(Method.POST, URL_CREATE_USER, null, Response.Listener { response ->
                try {
                    saveUserData(response)
                    complete(true)
                } catch (e: JSONException) {
                    Log.d("JSON", e.localizedMessage)
                    complete(false)
                }
            }, Response.ErrorListener { error ->
                Log.d("CREATE USER ERROR", "could not add user $error")
                complete(false)
            }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put(PARAM_AUTHORIZATION, "Bearer $authToken")
                return headers
            }
        }
        Volley.newRequestQueue(context).add(createUserRequest)

    }

    fun findUserByEmail(context: Context, complete: (Boolean) -> Unit) {

        val findUserRequest = object : JsonObjectRequest(
            Method.GET,
            "$URL_GET_USER$userEmail",
            null,
            Response.Listener { response ->
                try {
                    saveUserData(response)

                    val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                    LocalBroadcastManager.getInstance(context).sendBroadcast(userDataChange)
                    complete(true)
                } catch (e: JSONException) {
                    Log.d("JSON", e.localizedMessage)
                    complete(false)
                }
            },
            Response.ErrorListener { error ->
                Log.d("FIND USER ERROR", "could not find user $error")
                complete(false)
            }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers.put(PARAM_AUTHORIZATION, "Bearer $authToken")
                return headers
            }
        }
        Volley.newRequestQueue(context).add(findUserRequest)
    }

    fun saveUserData(response: JSONObject) {
        UserDataService.name = response.getString(PARAM_NAME)
        UserDataService.email = response.getString(PARAM_EMAIL)
        UserDataService.avatarName = response.getString(PARAM_AVATAR_NAME)
        UserDataService.avatarColor = response.getString(PARAM_AVATAR_COLOR)
        UserDataService.id = response.getString(PARAM_ID)
    }
}