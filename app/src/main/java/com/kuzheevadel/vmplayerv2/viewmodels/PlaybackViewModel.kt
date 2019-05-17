package com.kuzheevadel.vmplayerv2.viewmodels

import android.arch.lifecycle.ViewModel
import android.util.Log
import javax.inject.Inject

class PlaybackViewModel @Inject constructor(): ViewModel() {

    init {
        Log.i("ViewModelTest", "playback new instance")

    }

    override fun onCleared() {
        super.onCleared()
        Log.i("ViewModelTest", "Playback onCleared")
    }
}