package com.android.smack.model

data class Message(
    val message: String,
    val channelId: String,
    val userName: String,
    val avatar: String,
    val avatarColor: String,
    val messageId: String,
    val messageTime: String
)