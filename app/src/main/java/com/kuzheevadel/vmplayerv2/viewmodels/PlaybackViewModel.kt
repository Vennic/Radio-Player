package com.kuzheevadel.vmplayerv2.viewmodels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.kuzheevadel.vmplayerv2.interfaces.Interfaces
import com.kuzheevadel.vmplayerv2.model.Track
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class PlaybackViewModel @Inject constructor(private val mediaRepository: Interfaces.StorageMediaRepository): ViewModel() {

    val trackData: MutableLiveData<Track> = MutableLiveData()

    init {
        try {
            trackData.value = mediaRepository.getCurrentTrack()
        } catch (e: Exception) {

        }

        EventBus.getDefault().register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateUI(track: Track) {
        trackData.value = track
    }

    override fun onCleared() {
        EventBus.getDefault().unregister(this)
        super.onCleared()
    }
}