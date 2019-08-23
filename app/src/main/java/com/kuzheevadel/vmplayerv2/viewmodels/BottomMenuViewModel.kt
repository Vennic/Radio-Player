package com.kuzheevadel.vmplayerv2.viewmodels

import android.annotation.SuppressLint
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.media.MediaExtractor
import android.media.MediaFormat
import android.util.Log
import com.kuzheevadel.vmplayerv2.common.DataBaseInfo
import com.kuzheevadel.vmplayerv2.common.RewriteDoneMessage
import com.kuzheevadel.vmplayerv2.database.PlaylistDatabase
import com.kuzheevadel.vmplayerv2.interfaces.Interfaces
import com.kuzheevadel.vmplayerv2.model.Track
import com.kuzheevadel.vmplayerv2.model.TrackInfo
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class BottomMenuViewModel @Inject constructor(private val database: PlaylistDatabase,
                                              private val mediaRepository: Interfaces.StorageMediaRepository): ViewModel() {

    val trackInfoData: MutableLiveData<TrackInfo> = MutableLiveData()
    val dataBaseInfoData: MutableLiveData<DataBaseInfo> = MutableLiveData()
    private val mex = MediaExtractor()
    private lateinit var track: Track

    @SuppressLint("CheckResult")
    fun addOrDeleteFromDatabase() {
        if (!track.inPlaylist) {
            track.inPlaylist = true

            Completable.fromAction { database.trackDao().insertTrack(track) }
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        EventBus.getDefault().post(RewriteDoneMessage())
                        EventBus.getDefault().post("track")
                        mediaRepository.setFlagById(track.id, true)
                        dataBaseInfoData.postValue(DataBaseInfo.TRACK_ADDED)
                    },
                    {
                        dataBaseInfoData.postValue(DataBaseInfo.ERROR)
                    }
                )
        } else {
            track.inPlaylist = false

            Completable.fromAction { database.trackDao().deleteTrack(track) }
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        EventBus.getDefault().post(RewriteDoneMessage())
                        EventBus.getDefault().post("track")
                        mediaRepository.setFlagById(track.id, false)
                        mediaRepository.deleteTrackFromPlaylist(track.id)
                        dataBaseInfoData.postValue(DataBaseInfo.DELETED)
                    },
                    {
                        dataBaseInfoData.postValue(DataBaseInfo.ERROR)
                    }
                )
        }
    }

    fun getTrackInfo(position: Int, string: String) {
        track = mediaRepository.getTrackByPositionFromMainList(position)

        try {
            mex.setDataSource(track.data)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val mf: MediaFormat = mex.getTrackFormat(0)

        var bitrate = "320 $string"

        try {
            bitrate = "${mf.getInteger(MediaFormat.KEY_BIT_RATE) / 1000} $string"
        } catch (e: java.lang.Exception) {

        }

        val trackInfo = TrackInfo(track.artist,
            track.title, track.albumName,
            track.getDurationInTimeFormat(),
            "12 Mb",
            track.getImageUri(),
            bitrate,
            track.inPlaylist)

        Log.i("PlaylistTest", "getInfo fun: inPlaylist = ${track.inPlaylist}")

        trackInfoData.value = trackInfo
    }

}