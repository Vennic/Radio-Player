package com.kuzheevadel.vmplayerv2.model

import android.os.Parcel
import android.os.Parcelable

data class Track(val id: Long,
                 val title: String,
                 val artist: String,
                 val albumId: Long,
                 val duration: Int,
                 val albumName: String) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readInt(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(title)
        parcel.writeString(artist)
        parcel.writeLong(albumId)
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