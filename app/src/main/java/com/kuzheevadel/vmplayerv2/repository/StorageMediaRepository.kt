package com.kuzheevadel.vmplayerv2.repository

import android.util.Log
import com.kuzheevadel.vmplayerv2.common.Constants
import com.kuzheevadel.vmplayerv2.interfaces.Interfaces
import com.kuzheevadel.vmplayerv2.model.Album
import com.kuzheevadel.vmplayerv2.model.Track
import java.util.*

class StorageMediaRepository: Interfaces.StorageMediaRepository {
    
    private lateinit var origTracksList: MutableList<Track>
    private var shuffleMode = Constants.SHUFFLE_MODE_OFF
    private var currentPosition: Int = 0
    private var loopMode = Constants.NO_LOOP_MODE
    private lateinit var albumsList: MutableList<Album>


    init {
        Log.i("ViewModelTest", "MediaStore: $this")
    }
    override fun getTracksList(): MutableList<Track> {
        return origTracksList
    }

    private fun createAlbums() {
        val albumsMap = mutableMapOf<String, MutableList<Track>>()
        albumsList = mutableListOf()
        for (item in origTracksList) {
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
        origTracksList = list
        createAlbums()
    }
    
    override fun getCurrentTrack(): Track {
        return origTracksList[currentPosition]
    }

    override fun getNextTrack(): Track {
        when (loopMode) {
            Constants.NO_LOOP_MODE -> {
                return if (shuffleMode == Constants.SHUFFLE_MODE_ON) {
                    getShuffledTrack()
                } else {
                    if (currentPosition < origTracksList.size - 1) {
                        currentPosition++
                        origTracksList[currentPosition]
                    } else {
                        origTracksList[currentPosition]
                    }
                }
            }

            Constants.ONE_LOOP_MODE -> {
                return origTracksList[currentPosition]
            }

            Constants.ALL_LOOP_MODE -> {
                return if (shuffleMode == Constants.SHUFFLE_MODE_ON) {
                    getShuffledTrack()
                } else {
                    if (currentPosition < origTracksList.size - 1) {
                        currentPosition++
                        origTracksList[currentPosition]
                    } else {
                        currentPosition = 0
                        origTracksList[currentPosition]
                    }
                }
            }

            else -> return origTracksList[currentPosition]
        }
    }

    override fun getNextTrackByClick(): Track {
        return if (shuffleMode == Constants.SHUFFLE_MODE_ON) {
            getShuffledTrack()
        } else {
            if (currentPosition < origTracksList.size - 1) {
                currentPosition++
                origTracksList[currentPosition]
            } else {
                currentPosition = 0
                origTracksList[currentPosition]
            }
        }

    }

    override fun getPrevTrack(): Track {
        return if (shuffleMode == Constants.SHUFFLE_MODE_ON) {
            getShuffledTrack()
        } else {
            if (currentPosition > 0) {
                currentPosition--
                origTracksList[currentPosition]
            } else {
                currentPosition = origTracksList.size - 1
                origTracksList[currentPosition]
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
        currentPosition = r.nextInt(origTracksList.size)
        return origTracksList[currentPosition]
    }

}