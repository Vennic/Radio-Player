package com.kuzheevadel.vmplayerv2.viewmodels

import android.annotation.SuppressLint
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.net.Uri
import android.util.Log
import com.kuzheevadel.vmplayerv2.common.DataBaseInfo
import com.kuzheevadel.vmplayerv2.common.Source
import com.kuzheevadel.vmplayerv2.common.UpdateUIMessage
import com.kuzheevadel.vmplayerv2.database.PlaylistDatabase
import com.kuzheevadel.vmplayerv2.database.TrackDao
import com.kuzheevadel.vmplayerv2.interfaces.Interfaces
import com.kuzheevadel.vmplayerv2.repository.RadioRepository
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class PlaybackViewModel @Inject constructor(private val mediaRepository: Interfaces.StorageMediaRepository,
                                            private val radioRepository: RadioRepository,
                                            database: PlaylistDatabase): ViewModel() {

    val trackData: MutableLiveData<UpdateUIMessage> = MutableLiveData()
    private val trackDao: TrackDao = database.trackDao()
    val dataBaseInfoData: MutableLiveData<DataBaseInfo> = MutableLiveData()
    val checkPlaylistData: MutableLiveData<DataBaseInfo> = MutableLiveData()
    val trackIdData: MutableLiveData<Long> = MutableLiveData()

    lateinit var source: Source

    fun initViewModel() {
        try {
            when (source) {
                Source.TRACK -> {
                    val track = mediaRepository.getCurrentTrack()
                    with(track) {
                        trackData.value =
                            UpdateUIMessage(title, artist, albumId, null, duration, albumName, Source.TRACK, id, track.inPlaylist)
                    }

                    if (track.inPlaylist) {
                        checkPlaylistData.value = DataBaseInfo.ADDED
                    } else {
                        checkPlaylistData.value = DataBaseInfo.DONT_ADDED
                    }
                }
                Source.RADIO -> {
                    val radioStation = radioRepository.currentPlayingStation
                    trackData.value = UpdateUIMessage(
                        "",
                        radioStation!!.name,
                        0,
                        Uri.parse(radioStation.favicon),
                        0,
                        "",
                        Source.RADIO, -1, false)
                }
            }
        } catch (e: Exception) {
            Log.e("ErrorLogs", "playback init error", e)
        }

        EventBus.getDefault().register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateUI(message: UpdateUIMessage) {
        trackData.value = message

        Log.i("PLAYBACKTEST", message.inPlaylist.toString())
        if (message.inPlaylist) {
            checkPlaylistData.value = DataBaseInfo.ADDED
        } else {
            checkPlaylistData.value = DataBaseInfo.DONT_ADDED
        }
        trackIdData.value = mediaRepository.getCurrentTrack().id
    }

    @SuppressLint("CheckResult")
    fun addOrDeleteTrackFromPlaylist() {

        if (!mediaRepository.getCurrentTrack().inPlaylist) {
            mediaRepository.getCurrentTrack().inPlaylist = true

            Completable.fromAction { trackDao.insertTrack(mediaRepository.getCurrentTrack()) }
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        Log.i("InsertTest", "in onNext")
                        EventBus.getDefault().post("post")
                        mediaRepository.setFlagById(mediaRepository.getCurrentTrack().id, true)
                        dataBaseInfoData.postValue(DataBaseInfo.ADDED)
                    },
                    {
                        Log.e("InsertTest", "Add error", it)
                        dataBaseInfoData.postValue(DataBaseInfo.ERROR)
                    }
                )
        } else {
            mediaRepository.getCurrentTrack().inPlaylist = false

            Completable.fromAction { trackDao.deleteTrack(mediaRepository.getCurrentTrack()) }
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        EventBus.getDefault().post("post")
                        mediaRepository.setFlagById(mediaRepository.getCurrentTrack().id, false)
                        mediaRepository.deleteTrackFromPlaylist(mediaRepository.getCurrentTrack().id)
                        dataBaseInfoData.postValue(DataBaseInfo.DELETED)
                    },
                    {
                        dataBaseInfoData.postValue(DataBaseInfo.ERROR)
                    }
                )
        }
    }

    override fun onCleared() {
        EventBus.getDefault().unregister(this)
        super.onCleared()
    }
}