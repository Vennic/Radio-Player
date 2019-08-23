package com.kuzheevadel.vmplayerv2.repository

import android.util.Log
import com.kuzheevadel.vmplayerv2.common.Constants
import com.kuzheevadel.vmplayerv2.interfaces.Interfaces
import com.kuzheevadel.vmplayerv2.model.Album
import com.kuzheevadel.vmplayerv2.model.Track
import java.util.*

class StorageMediaRepository: Interfaces.StorageMediaRepository {
    
    private var loadedTracksList: MutableList<Track> = mutableListOf()
    override var isPlaylist = false
    private var shuffleMode = Constants.SHUFFLE_MODE_OFF
    private var currentTrackPosition: Int = 0
    private var loopMode = Constants.NO_LOOP_MODE
    private lateinit var albumsList: MutableList<Album>
    private lateinit var playingTrackList: MutableList<Track>
    private var deletedTrack: Track? = null
    private var isDeleted = false

    override fun getTracksList(): MutableList<Track> {
        return loadedTracksList
    }

    override fun setPlayingTrackList(trackList: MutableList<Track>) {
        val id = playingTrackList[currentTrackPosition].id
        currentTrackPosition = 0
        playingTrackList = trackList
        isDeleted = false
        Log.i("MOVETEST", "StorageMedia: $playingTrackList")


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

    override fun getTrackByPositionFromMainList(position: Int): Track {
        for (item in loadedTracksList) {
            Log.i("PlaylistTest", item.toString())
        }
        return loadedTracksList[position]
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
    }

    override fun deleteTrackFromPlaylist(id: Long) {
        if (isPlaylist) {
            for ((index, item) in playingTrackList.withIndex())
                if (item.id == id) {
                    deletedTrack = item

                    currentTrackPosition = 0


                    isDeleted = true
                    playingTrackList.removeAt(index)

                    return
                }
        }
    }

    override fun setShuffleMode(mode: Int) {
        shuffleMode = mode
    }
    
    override fun getCurrentTrack(): Track {
        return if (isDeleted) {
            deletedTrack!!
        } else {
            playingTrackList[currentTrackPosition]
        }
    }

    override fun getTrackByPosition(position: Int): Track {
        isDeleted = false
        Log.i("MOVETEST", "Get by position:  ${playingTrackList[position]}, position: $position")

        return playingTrackList[position]
    }

    override fun getNextTrack(): Track {
        isDeleted = false
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
        isDeleted = false
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
        isDeleted = false
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
        isDeleted = false
        var track = Track(0,"","",0, 0, 0, "", 0L, false, "")

        if (loadedTracksList.size > 0) {
            track = loadedTracksList[0]

            for ((index, item) in loadedTracksList.withIndex()) {
                if (item.id == id) {
                    track = item
                    currentTrackPosition = index
                    return track
                }
            }
        }

        return track
    }

    override fun setCurrentPosition(position: Int) {
        currentTrackPosition = position
    }

    override fun setAllFlagsFalse() {
        for (i in loadedTracksList) {
            i.inPlaylist = false
        }
    }

    override fun setPlaylistFlagsInLoadedList(list: MutableList<Track>) {
        for (playlistItem in list) {
            for (loadedItem in loadedTracksList) {
                if (playlistItem.id == loadedItem.id) {
                    loadedItem.inPlaylist = true
                }
            }
        }
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

    override fun comparePlaylistWithUploaded(playlist: MutableList<Track>): MutableList<Track> {
        synchronized(this) {
            val correctPlaylist = mutableListOf<Track>()

            for (item in playlist) {
                for (loadedItem in loadedTracksList) {
                    if (item.title == loadedItem.title && item.artist == loadedItem.artist && item.albumName == loadedItem.albumName) {
                        loadedItem.databaseId = item.databaseId
                        correctPlaylist.add(item)
                    }
                }
            }

            return correctPlaylist
        }
    }

    private fun getShuffledTrack(): Track {
        val r = Random()
        currentTrackPosition = r.nextInt(playingTrackList.size)
        return playingTrackList[currentTrackPosition]
    }

}