package com.kuzheevadel.vmplayerv2.viewmodels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.kuzheevadel.vmplayerv2.database.PlaylistDatabase
import com.kuzheevadel.vmplayerv2.model.Track
import com.kuzheevadel.vmplayerv2.common.State
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Callable
import javax.inject.Inject

class PlaylistViewModel @Inject constructor(private val database: PlaylistDatabase): ViewModel() {

    private val tracksDao = database.trackDao()
    val trackData: MutableLiveData<MutableList<Track>> = MutableLiveData()
    val loadStatus: MutableLiveData<State> = MutableLiveData()

    fun loadPlaylistFromDatabase() {
        val callable = Callable<MutableList<Track>> { tracksDao.getAllTracks() }

        Observable.fromCallable(callable)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                trackData.value = it
                loadStatus.value = State.DONE
            },
                {
                    loadStatus.value = State.ERROR
                })
    }

    override fun onCleared() {
        super.onCleared()
    }
}