package com.kuzheevadel.vmplayerv2.viewmodels

import android.annotation.SuppressLint
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.kuzheevadel.vmplayerv2.database.PlaylistDatabase
import com.kuzheevadel.vmplayerv2.database.TrackDao
import com.kuzheevadel.vmplayerv2.interfaces.Interfaces
import com.kuzheevadel.vmplayerv2.model.Track
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class PlaybackViewModel @Inject constructor(private val mediaRepository: Interfaces.StorageMediaRepository,
                                            database: PlaylistDatabase): ViewModel() {

    val trackData: MutableLiveData<Track> = MutableLiveData()
    private val trackDao: TrackDao

    init {
        try {
            trackData.value = mediaRepository.getCurrentTrack()
        } catch (e: Exception) {

        }
        trackDao = database.trackDao()

        EventBus.getDefault().register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateUI(track: Track) {
        trackData.value = track
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