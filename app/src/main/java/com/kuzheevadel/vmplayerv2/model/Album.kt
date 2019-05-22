package com.kuzheevadel.vmplayerv2.model

import android.net.Uri

data class Album(val title: String, val tracksList: MutableList<Track>) {

    fun getAlbumImageUri(): Uri {
        return tracksList[0].albumId
    }
}