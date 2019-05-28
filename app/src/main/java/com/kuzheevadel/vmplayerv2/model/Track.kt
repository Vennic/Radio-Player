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
                 val albumName: String) : Parcelable {


    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(Uri::class.java.classLoader),
        parcel.readParcelable(Uri::class.java.classLoader),
        parcel.readInt(),
        parcel.readString()
    )

    fun getFullName() = "$artist - $title"

    fun getNameAndDuration(): String {
        return "$artist • ${getDurationInTimeFormat()}"
    }

    fun getDurationInTimeFormat(): String {
        val d = duration / 1000
        val minutes = d / 60
        val seconds = d % 60
        return "$minutes:${if (seconds < 10) "0" else ""}$seconds"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(title)
        parcel.writeString(artist)
        parcel.writeParcelable(albumId, flags)
        parcel.writeParcelable(uri, flags)
        parcel.writeInt(duration)
        parcel.writeString(albumName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Track> {
        override fun createFromParcel(parcel: Parcel): Track {
            return Track(parcel)
        }

        override fun newArray(size: Int): Array<Track?> {
            return arrayOfNulls(size)
        }
    }
}