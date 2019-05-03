package com.kuzheevadel.vmplayerv2.presenters

import android.annotation.SuppressLint
import com.kuzheevadel.vmplayerv2.interfaces.MvpContracts
import com.kuzheevadel.vmplayerv2.model.Track
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class AllTracksPresenter(private val storageMedia: MvpContracts.StorageMedia): MvpContracts.AllTracksPresenter {

    private lateinit var mAdapter: MvpContracts.TracksAdapter

    override fun setAdapter(adapter: MvpContracts.TracksAdapter) {
        mAdapter = adapter
    }

    @SuppressLint("CheckResult")
    override fun updateAdapter(list: MutableList<Track>) {
        storageMedia.getTracksList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                mAdapter.updateTracksList(it)
            }
    }
}