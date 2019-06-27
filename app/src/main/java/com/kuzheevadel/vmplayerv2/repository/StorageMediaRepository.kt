package com.kuzheevadel.vmplayerv2.repository

import android.util.Log
import com.kuzheevadel.vmplayerv2.common.Constants
import com.kuzheevadel.vmplayerv2.common.LoadStateMessage
import com.kuzheevadel.vmplayerv2.interfaces.Interfaces
import com.kuzheevadel.vmplayerv2.model.Album
import com.kuzheevadel.vmplayerv2.model.Track
import org.greenrobot.eventbus.EventBus
import java.util.*

class StorageMediaRepository: Interfaces.StorageMediaRepository {
    
    private var loadedTracksList: MutableList<Track> = mutableListOf()
    override var isPlaylist = false
    private var shuffleMode = Constants.SHUFFLE_MODE_OFF
    private var currentTrackPosition: Int = 0
    private var loopMode = Constants.NO_LOOP_MODE
    private lateinit var albumsList: MutableList<Album>
    private lateinit var playingTrackList: MutableList<Track>

    override fun getTracksList(): MutableList<Track> {
        return loadedTracksList
    }

    override fun setPlayingTrackList(trackList: MutableList<Track>) {
        val id = playingTrackList[currentTrackPosition].id
        currentTrackPosition = 0
        playingTrackList = trackList

        for ((index, value) in playingTrackList.withIndex()) {
            if (value.id == id) {
                currentTrackPosition = index
                return
            }
        }
    }

    override fun createAlbums() {
        val albumsMap = mutableMapOf<String, MutableList<Track>>()
        albumsList = mutableListOf()
        for (item in loadedTracksList) {
            if (!albumsMap.containsKey(item.albumName)) {
                albumsMap[item.albumName] = mutableListOf(item)
            } else {
                albumsMap.getValue(item.albumName).add(item)
            }
        }

        for (item in albumsMap.entries) {
            val album = Album(item.key, item.value)
            albumsList.add(album)
        }
    }

    override fun setFlagById(id: Long, isAdded: Boolean) {
        for (item in loadedTracksList) {
            if (item.id == id)
                item.inPlaylist = isAdded
        }
    }

    override fun setTracksList(list: MutableList<Track>) {
        list.sortWith(compareBy { it.title })
        loadedTracksList = list
        playingTrackList = loadedTracksList
        createAlbums()
        EventBus.getDefault().post(LoadStateMessage(isTracksLoaded = true, isConnected = false))
    }

    override fun deleteTrackFromPlaylist(id: Long) {
        if (isPlaylist) {
            for ((index, item) in playingTrackList.withIndex())
                if (item.id == id)
                    playingTrackList.removeAt(index)
        }
        Log.i("STORAGEETST", playingTrackList.size.toString())
    }

    override fun setShuffleMode(mode: Int) {
        shuffleMode = mode
    }
    
    override fun getCurrentTrack(): Track {
        return playingTrackList[currentTrackPosition]
    }

    override fun getTrackByPosition(position: Int): Track {
        return playingTrackList[position]
    }

    override fun getNextTrack(): Track {
        when (loopMode) {
            Constants.NO_LOOP_MODE -> {
                return if (shuffleMode == Constants.SHUFFLE_MODE_ON) {
                    getShuffledTrack()
                } else {
                    if (currentTrackPosition < playingTrackList.size - 1) {
                        currentTrackPosition++
                        playingTrackList[currentTrackPosition]
                    } else {
                        playingTrackList[currentTrackPosition]
                    }
                }
            }

            Constants.ONE_LOOP_MODE -> {
                return playingTrackList[currentTrackPosition]
            }

            Constants.ALL_LOOP_MODE -> {
                return if (shuffleMode == Constants.SHUFFLE_MODE_ON) {
                    getShuffledTrack()
                } else {
                    if (currentTrackPosition < playingTrackList.size - 1) {
                        currentTrackPosition++
                        playingTrackList[currentTrackPosition]
                    } else {
                        currentTrackPosition = 0
                        playingTrackList[currentTrackPosition]
                    }
                }
            }

            else -> return playingTrackList[currentTrackPosition]
        }
    }

    override fun getNextTrackByClick(): Track {
        return if (shuffleMode == Constants.SHUFFLE_MODE_ON) {
            getShuffledTrack()
        } else {
            if (currentTrackPosition < playingTrackList.size - 1) {
                currentTrackPosition++
                playingTrackList[currentTrackPosition]
            } else {
                currentTrackPosition = 0
                playingTrackList[currentTrackPosition]
            }
        }

    }

    override fun getPrevTrack(): Track {
        return if (shuffleMode == Constants.SHUFFLE_MODE_ON) {
            getShuffledTrack()
        } else {
            if (currentTrackPosition > 0) {
                currentTrackPosition--
                playingTrackList[currentTrackPosition]
            } else {
                currentTrackPosition = playingTrackList.size - 1
                playingTrackList[currentTrackPosition]
            }
        }
    }

    override fun getTrackById(id: Long): Track {
        var track = loadedTracksList[0]

        for ((index, item) in loadedTracksList.withIndex()) {
            if (item.id == id) {
                track = item
                currentTrackPosition = index
                return track
            }
        }

        return track
    }

    override fun setCurrentPosition(position: Int) {
        currentTrackPosition = position
    }

    override fun setPlaylistFlagsInLoadedList(list: MutableList<Track>) {
        for (playlistItem in list) {
            for (loadedItem in loadedTracksList) {
                if (playlistItem.id == loadedItem.id) {
                    loadedItem.inPlaylist = true
                }
            }
        }

        Log.i("PLAYLISTTEST", "finish sort")
    }

    override fun setPlaylistFlagsInAlbumsList(list: MutableList<Track>) {
        for (albumsItem in list)
            for (loadedItem in loadedTracksList)
                if (albumsItem.id == loadedItem.id)
                    albumsItem.inPlaylist = loadedItem.inPlaylist
    }

    override fun getCurrentPosition() = currentTrackPosition

    override fun setLoopMode(mode: Int) {
        loopMode = mode
    }

    override fun getAlbumsList(): MutableList<Album> {
        return albumsList
    }

    private fun getShuffledTrack(): Track {
        val r = Random()
        currentTrackPosition = r.nextInt(playingTrackList.size)
        return playingTrackList[currentTrackPosition]
    }

}