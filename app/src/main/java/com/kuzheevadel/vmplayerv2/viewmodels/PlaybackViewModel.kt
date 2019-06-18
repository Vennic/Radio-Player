package com.kuzheevadel.vmplayerv2.viewmodels

import android.annotation.SuppressLint
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.net.Uri
import android.util.Log
import com.kuzheevadel.vmplayerv2.common.Source
import com.kuzheevadel.vmplayerv2.common.UpdateUIMessage
import com.kuzheevadel.vmplayerv2.database.PlaylistDatabase
import com.kuzheevadel.vmplayerv2.database.TrackDao
import com.kuzheevadel.vmplayerv2.interfaces.Interfaces
import com.kuzheevadel.vmplayerv2.model.Track
import com.kuzheevadel.vmplayerv2.repository.RadioRepository
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class PlaybackViewModel @Inject constructor(private val mediaRepository: Interfaces.StorageMediaRepository,
                                            private val radioRepository: RadioRepository,
                                            private val database: PlaylistDatabase): ViewModel() {

    val trackData: MutableLiveData<UpdateUIMessage> = MutableLiveData()
    private val trackDao: TrackDao = database.trackDao()
    lateinit var source: Source

    fun initViewModel() {
        try {
            when (source) {
                Source.TRACK -> {
                    val track = mediaRepository.getCurrentTrack()

                    with(track) {
                        trackData.value =
                            UpdateUIMessage(title, artist, albumId, null, duration, albumName, Source.TRACK)
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
                        Source.RADIO)
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
    }

    @SuppressLint("CheckResult")
    fun addTrackToPlaylistDatabase() {
        Completable.fromAction { trackDao.insertTrack(mediaRepository.getCurrentTrack()) }
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    Log.i("InsertTest", "in onNext")
                    EventBus.getDefault().post("post")
                },
                {

                }
            )
    }

    fun deleteTrackFromPlaylistDatabase() {
        Completable.fromAction { trackDao.deleteTrack(mediaRepository.getCurrentTrack()) }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    override fun onCleared() {
        EventBus.getDefault().unregister(this)
        super.onCleared()
    }
}