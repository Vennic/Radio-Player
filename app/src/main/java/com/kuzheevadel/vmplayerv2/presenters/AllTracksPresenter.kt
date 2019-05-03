package com.kuzheevadel.vmplayerv2.presenters

import android.annotation.SuppressLint
import com.kuzheevadel.vmplayerv2.interfaces.MvpContracts
import com.kuzheevadel.vmplayerv2.model.Track
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Callable

class AllTracksPresenter(private val storageMedia: Callable<MutableList<Track>>,
                         private val mediaRepository: MvpContracts.StorageMediaRepository): MvpContracts.AllTracksPresenter {

    private lateinit var mAdapter: MvpContracts.TracksAdapter

    override fun setAdapter(adapter: MvpContracts.TracksAdapter) {
        mAdapter = adapter
    }

    @SuppressLint("CheckResult")
    override fun updateAdapter() {
        Observable.fromCallable(storageMedia)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                mAdapter.updateTracksList(it)
                mediaRepository.setTracksList(it)
            }
    }
}