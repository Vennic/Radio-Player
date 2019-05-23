package com.kuzheevadel.vmplayerv2.radio

data class RadioStation(
    val categories: List<Category>,
    val country: String,
    val created_at: String,
    val facebook: Any,
    val id: Int,
    val image: Image,
    val name: String,
    val slug: String,
    val streams: List<Stream>,
    val total_listeners: Int,
    val twitter: Any,
    val updated_at: String,
    val website: String
)