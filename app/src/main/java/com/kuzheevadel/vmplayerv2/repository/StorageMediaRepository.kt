package com.kuzheevadel.vmplayerv2.repository

import android.util.Log
import com.kuzheevadel.vmplayerv2.common.Constants
import com.kuzheevadel.vmplayerv2.interfaces.Interfaces
import com.kuzheevadel.vmplayerv2.model.Album
import com.kuzheevadel.vmplayerv2.model.Track
import java.util.*

class StorageMediaRepository: Interfaces.StorageMediaRepository {
    
    private lateinit var loadedTracksList: MutableList<Track>
    private var shuffleMode = Constants.SHUFFLE_MODE_ON
    private var currentPosition: Int = 0
    private var loopMode = Constants.NO_LOOP_MODE
    private lateinit var albumsList: MutableList<Album>
    private lateinit var playingTrackList: MutableList<Track>

    init {
        Log.i("ViewModelTest", "MediaStore: $this")
    }
    override fun getTracksList(): MutableList<Track> {
        return loadedTracksList
    }

    override fun setPlayingTrackList(trackList: MutableList<Track>) {
        playingTrackList = trackList
    }

    private fun createAlbums() {
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
        Log.i("CheckMap", "$albumsMap")
    }

    override fun setTracksList(list: MutableList<Track>) {
        list.sortWith(compareBy { it.title })
        loadedTracksList = list
        createAlbums()
    }

    override fun setShuffleMode(mode: Int) {
        shuffleMode = mode
    }
    
    override fun getCurrentTrack(): Track {
        return playingTrackList[currentPosition]
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
                    if (currentPosition < playingTrackList.size - 1) {
                        currentPosition++
                        playingTrackList[currentPosition]
                    } else {
                        playingTrackList[currentPosition]
                    }
                }
            }

            Constants.ONE_LOOP_MODE -> {
                return playingTrackList[currentPosition]
            }

            Constants.ALL_LOOP_MODE -> {
                return if (shuffleMode == Constants.SHUFFLE_MODE_ON) {
                    getShuffledTrack()
                } else {
                    if (currentPosition < playingTrackList.size - 1) {
                        currentPosition++
                        playingTrackList[currentPosition]
                    } else {
                        currentPosition = 0
                        playingTrackList[currentPosition]
                    }
                }
            }

            else -> return playingTrackList[currentPosition]
        }
    }

    override fun getNextTrackByClick(): Track {
        return if (shuffleMode == Constants.SHUFFLE_MODE_ON) {
            getShuffledTrack()
        } else {
            if (currentPosition < playingTrackList.size - 1) {
                currentPosition++
                playingTrackList[currentPosition]
            } else {
                currentPosition = 0
                playingTrackList[currentPosition]
            }
        }

    }

    override fun getPrevTrack(): Track {
        return if (shuffleMode == Constants.SHUFFLE_MODE_ON) {
            getShuffledTrack()
        } else {
            if (currentPosition > 0) {
                currentPosition--
                playingTrackList[currentPosition]
            } else {
                currentPosition = playingTrackList.size - 1
                playingTrackList[currentPosition]
            }
        }
    }

    override fun setCurrentPosition(position: Int) {
        currentPosition = position
    }

    override fun getCurrentPosition() = currentPosition

    override fun setLoopMode(mode: Int) {
        loopMode = mode
    }

    override fun getAlbumsList(): MutableList<Album> {
        return albumsList
    }

    private fun getShuffledTrack(): Track {
        val r = Random()
        currentPosition = r.nextInt(playingTrackList.size)
        return playingTrackList[currentPosition]
    }

}