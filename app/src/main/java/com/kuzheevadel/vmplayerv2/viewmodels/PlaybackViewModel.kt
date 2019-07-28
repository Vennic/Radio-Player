package com.kuzheevadel.vmplayerv2.viewmodels

import android.annotation.SuppressLint
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.net.Uri
import android.util.Log
import com.kuzheevadel.vmplayerv2.common.DataBaseInfo
import com.kuzheevadel.vmplayerv2.common.Source
import com.kuzheevadel.vmplayerv2.common.UpdateUIMessage
import com.kuzheevadel.vmplayerv2.database.PlaylistDatabase
import com.kuzheevadel.vmplayerv2.database.RadioDatabase
import com.kuzheevadel.vmplayerv2.database.TrackDao
import com.kuzheevadel.vmplayerv2.interfaces.Interfaces
import com.kuzheevadel.vmplayerv2.model.RadioStation
import com.kuzheevadel.vmplayerv2.repository.RadioRepository
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.Callable
import javax.inject.Inject

class PlaybackViewModel @Inject constructor(private val mediaRepository: Interfaces.StorageMediaRepository,
                                            private val radioRepository: RadioRepository,
                                            tracksDatabase: PlaylistDatabase,
                                            radioDatabase: RadioDatabase): ViewModel() {

    val trackData: MutableLiveData<UpdateUIMessage> = MutableLiveData()
    private val trackDao: TrackDao = tracksDatabase.trackDao()
    private val radioDao = radioDatabase.radioDao()
    val dataBaseInfoData: MutableLiveData<DataBaseInfo> = MutableLiveData()
    val checkPlaylistData: MutableLiveData<DataBaseInfo> = MutableLiveData()

    lateinit var source: Source

    fun initViewModel() {
        try {
            when (source) {
                Source.TRACK -> {
                    val track = mediaRepository.getCurrentTrack()
                    with(track) {
                        trackData.value =
                            UpdateUIMessage(title, artist, albumId, null, duration, albumName, Source.TRACK, id, track.inPlaylist)
                    }

                    if (track.inPlaylist) {
                        checkPlaylistData.value = DataBaseInfo.TRACK_ADDED
                    } else {
                        checkPlaylistData.value = DataBaseInfo.TRACK_IS_NOT_ADDED
                    }
                }
                Source.RADIO -> {
                    val radioStation = radioRepository.currentPlayingStation
                    checkRadioInDatabase(radioStation?.id?.toLong() ?: 0)

                    trackData.value = UpdateUIMessage(
                        "",
                        radioStation!!.name,
                        0,
                        Uri.parse(radioStation.favicon),
                        0,
                        "",
                        Source.RADIO, -1, radioStation.inPlaylist)
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

        if (message.type == Source.TRACK) {
            source = Source.TRACK

            if (message.inPlaylist) {
                checkPlaylistData.value = DataBaseInfo.TRACK_ADDED
            } else {
                checkPlaylistData.value = DataBaseInfo.TRACK_IS_NOT_ADDED
            }
        } else {
            source = Source.RADIO
            checkRadioInDatabase(message.id)
        }
    }

    @SuppressLint("CheckResult")
    private fun checkRadioInDatabase(radioId: Long) {
        val callable = Callable<RadioStation>{ radioDao.getRadiostationById(radioId.toString())}

        Observable.fromCallable(callable)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    if (it != null) {
                        checkPlaylistData.value = DataBaseInfo.RADIO_ADDED
                        radioRepository.currentPlayingStation?.inPlaylist = true
                    } else {
                        checkPlaylistData.value = DataBaseInfo.RADIO_IS_NOT_ADDED
                        radioRepository.currentPlayingStation?.inPlaylist = false

                    }
                },
                {
                    checkPlaylistData.value = DataBaseInfo.RADIO_IS_NOT_ADDED
                    radioRepository.currentPlayingStation?.inPlaylist = false

                }
            )
    }

    @SuppressLint("CheckResult")
    fun addOrDeleteTrackFromPlaylist() {

        if (source == Source.TRACK) {
            if (!mediaRepository.getCurrentTrack().inPlaylist) {
                mediaRepository.getCurrentTrack().inPlaylist = true

                Completable.fromAction { trackDao.insertTrack(mediaRepository.getCurrentTrack()) }
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        {
                            EventBus.getDefault().post("track")
                            mediaRepository.setFlagById(mediaRepository.getCurrentTrack().id, true)
                            dataBaseInfoData.postValue(DataBaseInfo.TRACK_ADDED)
                        },
                        {
                            dataBaseInfoData.postValue(DataBaseInfo.ERROR)
                        }
                    )
            } else {
                mediaRepository.getCurrentTrack().inPlaylist = false

                Completable.fromAction { trackDao.deleteTrack(mediaRepository.getCurrentTrack()) }
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        {
                            EventBus.getDefault().post("track")
                            mediaRepository.setFlagById(mediaRepository.getCurrentTrack().id, false)
                            mediaRepository.deleteTrackFromPlaylist(mediaRepository.getCurrentTrack().id)
                            dataBaseInfoData.postValue(DataBaseInfo.DELETED)
                        },
                        {
                            dataBaseInfoData.postValue(DataBaseInfo.ERROR)
                        }
                    )
            }
        } else if (source == Source.RADIO) {
            if (!radioRepository.currentPlayingStation!!.inPlaylist) {
                Completable.fromAction { radioDao.insertTrack(radioRepository.currentPlayingStation!!) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            dataBaseInfoData.value = DataBaseInfo.RADIO_ADDED
                            radioRepository.currentPlayingStation?.inPlaylist = true
                            EventBus.getDefault().post("radio")
                        },
                        {
                            dataBaseInfoData.value = DataBaseInfo.ERROR
                        }
                    )
            } else {
                Completable.fromAction { radioDao.deleteTrack(radioRepository.currentPlayingStation!!) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            dataBaseInfoData.value = DataBaseInfo.RADIO_IS_NOT_ADDED
                            radioRepository.currentPlayingStation?.inPlaylist = false
                            EventBus.getDefault().post("radio")
                        },
                        {
                            dataBaseInfoData.value = DataBaseInfo.ERROR
                        }
                    )
            }
        }
    }

    override fun onCleared() {
        EventBus.getDefault().unregister(this)
        super.onCleared()
    }
}