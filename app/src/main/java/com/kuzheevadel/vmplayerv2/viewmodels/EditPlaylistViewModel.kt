package com.kuzheevadel.vmplayerv2.viewmodels

import android.annotation.SuppressLint
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.kuzheevadel.vmplayerv2.common.RewriteDoneMessage
import com.kuzheevadel.vmplayerv2.common.State
import com.kuzheevadel.vmplayerv2.database.PlaylistDatabase
import com.kuzheevadel.vmplayerv2.interfaces.Interfaces
import com.kuzheevadel.vmplayerv2.model.Track
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.Callable
import javax.inject.Inject

class EditPlaylistViewModel @Inject constructor(database: PlaylistDatabase,
                                                private val mediaRepository: Interfaces.StorageMediaRepository): ViewModel() {

    private val tracksDao = database.trackDao()
    val trackData: MutableLiveData<MutableList<Track>> = MutableLiveData()
    val loadStatus: MutableLiveData<State> = MutableLiveData()
    var trackList = mutableListOf<Track>()
    private val disposable = CompositeDisposable()
    private var isLoadedBefore = false

    @SuppressLint("CheckResult")
    fun loadPlaylistFromDatabase() {
        val callable = Callable<MutableList<Track>> {
            val playlist = tracksDao.getAllTracks()
            mediaRepository.comparePlaylistWithUploaded(playlist)
        }
        loadStatus.value = State.LOADING

        disposable.add(
            Observable.fromCallable(callable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (!isLoadedBefore) {
                        isLoadedBefore = true
                    }

                    trackData.value = it
                    loadStatus.value = State.DONE
                },
                    {

                        Log.e("PLAYLISTERROR", "", it)
                        loadStatus.value = State.ERROR
                    }))
    }

    fun overwriteDatabase() {
        val callable = Callable{
            for ((index, item) in trackList.withIndex()) {
                item.databaseId = index.toLong() + 1000
            }

            tracksDao.deleteAllTracks()
        }

        disposable.add(
            Completable.fromCallable(callable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        writeTracksInDatabase(trackList)
                    }
                ,
                    {
                        loadStatus.value = State.ERROR
                    }
                )
        )

    }

    private fun writeTracksInDatabase(trackList: MutableList<Track>) {
        val callable = Callable { tracksDao.insertTracksList(trackList) }

        disposable.add(
            Completable.fromCallable(callable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        EventBus.getDefault().post(RewriteDoneMessage(true))
                        loadStatus.value = State.DONE
                        disposable.dispose()
                    }
                ,
                    {
                        loadStatus.value = State.ERROR
                    }
                )
        )
    }
}