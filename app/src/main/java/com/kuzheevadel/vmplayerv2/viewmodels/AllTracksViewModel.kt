package com.kuzheevadel.vmplayerv2.viewmodels

import android.annotation.SuppressLint
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.kuzheevadel.vmplayerv2.adapters.TrackListAdapter
import com.kuzheevadel.vmplayerv2.common.Constants
import com.kuzheevadel.vmplayerv2.common.LoadMediaMessage
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

class AllTracksViewModel @Inject constructor(private val storageMedia: Callable<MutableList<Track>>,
                                             private val mediaRepository: Interfaces.StorageMediaRepository,
                                             private val database: PlaylistDatabase): ViewModel() {

    private lateinit var mAdapter: TrackListAdapter

    val loadStateData: MutableLiveData<State> = MutableLiveData()

    @SuppressLint("CheckResult")
    fun loadTracks(loadState: Int) {
        loadStateData.value = State.LOADING
        if (mediaRepository.getTracksList().isEmpty()) {
            Observable.fromCallable(storageMedia)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    mediaRepository.setTracksList(it)

                    when (loadState) {
                        Constants.LOADED_BEFORE -> EventBus.getDefault().postSticky("before")
                        Constants.FIRST_LOAD -> EventBus.getDefault().post("first")
                    }

                    mAdapter.trackList = mediaRepository.getTracksList()
                    mAdapter.notifyDataSetChanged()

                    if (it != null) {
                        EventBus.getDefault().post(LoadMediaMessage(true))
                        loadStateData.value = State.DONE
                    } else {
                        loadStateData.value = State.ERROR
                    }

                    val callable = Callable<Unit> {mediaRepository.setPlaylistFlagsInLoadedList(database.trackDao().getAllTracks())}
                    val disposable = CompositeDisposable()

                    disposable.add(Completable.fromCallable(callable)
                        .subscribeOn(Schedulers.computation())
                        .subscribe ({
                            disposable.dispose()
                        },
                            {error ->
                                Log.e("PLAYLISTERROR", "", error)
                            }))
                },

                    {
                        loadStateData.value = State.ERROR
                    })
        } else {
            mediaRepository.createAlbums()
            loadStateData.value = State.DONE
            mAdapter.trackList = mediaRepository.getTracksList()
            EventBus.getDefault().postSticky("post")
            mAdapter.notifyDataSetChanged()
            EventBus.getDefault().postSticky(LoadMediaMessage(true))
        }
    }

    fun setAdapter(adapter: TrackListAdapter) {
        mAdapter = adapter
    }

}