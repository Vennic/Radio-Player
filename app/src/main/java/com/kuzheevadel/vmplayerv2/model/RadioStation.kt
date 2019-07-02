package com.kuzheevadel.vmplayerv2.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.support.v7.util.DiffUtil
import android.util.Log

@Entity
data class RadioStation(
    val bitrate: String,
    val changeuuid: String,
    val clickcount: String,
    val clicktimestamp: String,
    val clicktrend: String,
    val codec: String,
    val country: String,
    val favicon: String?,
    val hls: String,
    val homepage: String,
    @PrimaryKey val id: String,
    val ip: String,
    val language: String,
    val lastchangetime: String,
    val lastcheckok: String,
    val lastcheckoktime: String,
    val lastchecktime: String,
    val name: String,
    val negativevotes: String,
    val state: String,
    val stationuuid: String,
    val tags: String,
    val url: String,
    val votes: String,
    var inPlaylist: Boolean = false
) {

    fun getCorrectUrl(): String {
        Log.i("UrlLog", url)
        val url2 = url.replace(".pls", "").replace("\\", "")
        Log.i("UrlLog", url2)
        return url2
    }

    fun getTagsInfo(): String {
        val correctTags = tags.replace(",", ", ").replace("\n", "")
        return "tags: $correctTags"
    }

    fun getCountryInfo() = "Country • $country"

    companion object {
        @JvmStatic
        val diffCallback = object : DiffUtil.ItemCallback<RadioStation>() {
            override fun areItemsTheSame(oldItem: RadioStation, newItem: RadioStation): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: RadioStation, newItem: RadioStation): Boolean {
                return oldItem == newItem
            }

        }
    }
}