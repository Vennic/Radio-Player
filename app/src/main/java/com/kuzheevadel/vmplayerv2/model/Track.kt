package com.kuzheevadel.vmplayerv2.model

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

data class Track(val id: Long,
                 val title: String,
                 val artist: String,
                 val albumId: Uri,
                 val uri: Uri,
                 val duration: Int,
                 val albumName: String)  {


    fun getFullName() = "$artist - $title"

    fun getNameAndDuration(): String {
        return "$artist • ${getDurationInTimeFormat()}"
    }

    private fun getDurationInTimeFormat(): String {
        val d = duration / 1000
        val minutes = d / 60
        val seconds = d % 60
        return "$minutes:${if (seconds < 10) "0" else ""}$seconds"
    }
}