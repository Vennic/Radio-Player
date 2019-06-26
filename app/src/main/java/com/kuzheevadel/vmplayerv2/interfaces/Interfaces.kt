package com.kuzheevadel.vmplayerv2.interfaces

import com.kuzheevadel.vmplayerv2.model.Album
import com.kuzheevadel.vmplayerv2.model.Track

class Interfaces {

    interface StorageMediaRepository {
        fun setFlagById(id: Long, isAdded: Boolean)
        fun setPlaylistFlags(list: MutableList<Track>)
        fun setShuffleMode(mode:Int)
        fun getTrackByPosition(position: Int): Track
        fun getTracksList(): MutableList<Track>
        fun setTracksList(list: MutableList<Track>)
        fun getCurrentTrack(): Track
        fun getNextTrack(): Track
        fun getNextTrackByClick(): Track
        fun getPrevTrack(): Track
        fun createAlbums()
        fun setCurrentPosition(position: Int) //???
        fun getCurrentPosition(): Int
        fun setLoopMode(mode: Int)
        fun getAlbumsList(): MutableList<Album>
        fun setPlayingTrackList(trackList: MutableList<Track>)
    }

}