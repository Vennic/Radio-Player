package com.kuzheevadel.vmplayerv2.viewmodels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.media.MediaExtractor
import android.media.MediaFormat
import android.util.Log
import com.kuzheevadel.vmplayerv2.database.PlaylistDatabase
import com.kuzheevadel.vmplayerv2.interfaces.Interfaces
import com.kuzheevadel.vmplayerv2.model.TrackInfo
import javax.inject.Inject

class BottomMenuViewModel @Inject constructor(database: PlaylistDatabase,
                                              private val mediaRepository: Interfaces.StorageMediaRepository): ViewModel() {

    var trackInfoData: MutableLiveData<TrackInfo> = MutableLiveData()
    private val mex = MediaExtractor()

    fun getTrackInfo(position: Int, string: String) {
        val track = mediaRepository.getTrackByPositionFromMainList(position)

        try {

            Log.i("MEXINFO", track.data)
            mex.setDataSource(track.data)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val mf = mex.getTrackFormat(0)
        Log.i("MEXINFO", "$mf")
        Log.i("MEXINFO", "${mf.getInteger(MediaFormat.KEY_BIT_RATE)}")

        val bitrate = "${mf.getInteger(MediaFormat.KEY_BIT_RATE) / 1000} $string"

        val trackInfo = TrackInfo(track.artist,
            track.title, track.albumName,
            track.getDurationInTimeFormat(),
            "12 Mb",
            track.getImageUri(),
            bitrate)

        trackInfoData.value = trackInfo
    }

}