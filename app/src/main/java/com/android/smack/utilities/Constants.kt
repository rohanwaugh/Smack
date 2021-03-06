package com.android.smack.utilities


//const val BASE_URL = "https://devslopes-chattin.herokuapp.com/v1/"
const val BASE_URL = "http://10.0.2.2:3005/v1/"
const val SOCKET_URL = "http://10.0.2.2:3005/"
const val URL_REGISTER = "${BASE_URL}account/register"
const val URL_LOGIN = "${BASE_URL}account/login"
const val URL_CREATE_USER = "${BASE_URL}user/add"
const val URL_GET_USER = "${BASE_URL}user/byEmail/"
const val URL_GET_CHANNEL = "${BASE_URL}channel"
const val URL_GET_MESSAGES = "${BASE_URL}message/byChannel/"

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
const val PARAM_MESSAGE_BODY = "messageBody"
const val PARAM_CHANNEL_ID ="channelId"
const val PARAM_USER_NAME = "userName"
const val PARAM_USER_AVATAR = "userAvatar"
const val PARAM_USER_AVATAR_COLOR = "userAvatarColor"
const val PARAM_MESSAGE_ID = "_id"
const val PARAM_MESSAGE_TIMESTAMP = "timeStamp"

//Broadcast receiver
const val BROADCAST_USER_DATA_CHANGE = "BROADCAST_USER_DATA_CHANGE"

//Socket
const val CHANNEL_EVENT = "newChannel"
const val CHANNEL_CREATED_EVENT = "channelCreated"
const val CHANNEL_NAME = "name"
const val CHANNEL_DESCRIPTION = "description"
const val CHANNEL_ID = "_id"
const val MESSAGE_EVENT = "newMessage"
const val MESSAGE_CREATED_EVENT = "messageCreated"

//Shared Preference
const val SHARED_PREF_FILENAME = "prefs"
const val SHARED_IS_LOGGED_IN = "isLoggedIn"
const val SHARED_AUTH_TOKEN = "authToken"
const val SHARED_USER_EMAIL = "userEmail"