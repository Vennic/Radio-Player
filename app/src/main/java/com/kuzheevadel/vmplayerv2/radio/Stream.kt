package com.kuzheevadel.vmplayerv2.radio

data class Stream(
    val bitrate: Int,
    val content_type: String,
    val listeners: Int,
    val status: Int,
    val stream: String
)