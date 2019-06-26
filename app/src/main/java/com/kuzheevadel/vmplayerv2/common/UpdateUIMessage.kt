package com.kuzheevadel.vmplayerv2.common

import android.content.ContentUris
import android.net.Uri

data class UpdateUIMessage(val title: String,
                           val artist: String?,
                           val albumId: Long,
                           val url: Uri?,
                           val duration: Int,
                           val albumName: String,
                           val type: Source,
                           val id: Long,
                           var inPlaylist: Boolean) {

    fun getFullName(): String {
        return when (type) {
            Source.TRACK -> "$artist - $title"
            Source.RADIO -> artist!!
        }
    }

    fun getImageUri(): Uri {
        return when (type) {
            Source.TRACK -> ContentUris.withAppendedId(Uri.parse(Constants.BASE_ALBUMSART_URI), albumId)
            Source.RADIO -> url!!
        }
    }

    fun getDurationInSeconds(): Int {
        return duration / 1000
    }

    fun getDurationType(): String {
        return when (type) {
            Source.RADIO -> "--:--"
            Source.TRACK -> getDurationInTimeFormat()
        }
    }

    private fun getDurationInTimeFormat(): String {
        val d = duration / 1000
        val minutes = d / 60
        val seconds = d % 60
        return "$minutes:${if (seconds < 10) "0" else ""}$seconds"
    }
}