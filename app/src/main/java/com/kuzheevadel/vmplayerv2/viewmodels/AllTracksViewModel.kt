package com.kuzheevadel.vmplayerv2.viewmodels

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModel
import com.kuzheevadel.vmplayerv2.adapters.TrackListAdapter
import com.kuzheevadel.vmplayerv2.common.LoadMediaMessage
import com.kuzheevadel.vmplayerv2.interfaces.Interfaces
import com.kuzheevadel.vmplayerv2.model.Track
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.Callable
import javax.inject.Inject

class AllTracksViewModel @Inject constructor(private val storageMedia: Callable<MutableList<Track>>,
                                             private val mediaRepository: Interfaces.StorageMediaRepository): ViewModel() {

    private lateinit var mAdapter: TrackListAdapter

    @SuppressLint("CheckResult")
    fun loadTracks() {
        if (mediaRepository.getTracksList().isEmpty()) {
            Observable.fromCallable(storageMedia)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    mediaRepository.setTracksList(it)
                    mAdapter.trackList = mediaRepository.getTracksList()
                    mAdapter.notifyDataSetChanged()

                    if (it != null) {
                        EventBus.getDefault().post(LoadMediaMessage(true))
                    } else {
                        EventBus.getDefault().post(LoadMediaMessage(false))
                    }
                }
        } else {
            mediaRepository.createAlbums()
            mAdapter.trackList = mediaRepository.getTracksList()
            mAdapter.notifyDataSetChanged()
            EventBus.getDefault().postSticky(LoadMediaMessage(true))
        }

    }

    fun setAdapter(adapter: TrackListAdapter) {
        mAdapter = adapter
    }

}