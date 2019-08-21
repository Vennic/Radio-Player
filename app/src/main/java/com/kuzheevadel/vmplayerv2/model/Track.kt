package com.kuzheevadel.vmplayerv2.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import com.kuzheevadel.vmplayerv2.common.Constants

@Entity
data class Track(val id: Long,
                 val title: String,
                 val artist: String,
                 val albumId: Long,
                 val uri: Long,
                 val duration: Int,
                 val albumName: String,
                 @PrimaryKey var databaseId: Long,
                 var inPlaylist: Boolean,
                 val data:String){

    fun getNameAndDuration(): String {
        return "$artist • ${getDurationInTimeFormat()}"
    }

    fun getAudioUri(): Uri {
        return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
    }

    fun getImageUri(): Uri {
        return ContentUris.withAppendedId(Uri.parse(Constants.BASE_ALBUMSART_URI), albumId)
    }


    fun getDurationInTimeFormat(): String {
        val d = duration / 1000
        val minutes = d / 60
        val seconds = d % 60
        return "$minutes:${if (seconds < 10) "0" else ""}$seconds"
    }

    override fun toString(): String {
        return "$title - $databaseId"
    }
}