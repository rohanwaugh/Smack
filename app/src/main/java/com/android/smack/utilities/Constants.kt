package com.android.smack.utilities


//const val BASE_URL = "https://devslopes-chattin.herokuapp.com/v1/"
const val BASE_URL = "http://10.0.2.2:3005/v1/"
const val URL_REGISTER = "${BASE_URL}account/register"
const val URL_LOGIN = "${BASE_URL}/account/login"
const val URL_CREATE_USER = "${BASE_URL}/user/add"
const val URL_GET_USER = "${BASE_URL}/user/byEmail/"
const val PARAM_EMAIL = "email"
const val PARAM_PASSWORD = "password"
const val PARAM_TOKEN = "token"
const val PARAM_USER = "user"
const val PARAM_NAME = "name"
const val PARAM_AVATAR_NAME= "avatarName"
const val PARAM_AVATAR_COLOR = "avatarColor"
const val PARAM_AUTHORIZATION = "Authorization"
const val PARAM_ID = "_id"
const val DRAWABLE = "drawable"

//Broadcast receiver
const val BROADCAST_USER_DATA_CHANGE = "BROADCAST_USER_DATA_CHANGE"