package com.kuzheevadel.vmplayerv2.interfaces

import com.kuzheevadel.vmplayerv2.model.Album
import com.kuzheevadel.vmplayerv2.model.Track

class Interfaces {

    interface StorageMediaRepository {
        var isPlaylist: Boolean
        fun setFlagById(id: Long, isAdded: Boolean)
        fun setPlaylistFlagsInLoadedList(list: MutableList<Track>)
        fun setPlaylistFlagsInAlbumsList(list: MutableList<Track>)
        fun setShuffleMode(mode:Int)
        fun getTrackByPosition(position: Int): Track
        fun getTracksList(): MutableList<Track>
        fun setTracksList(list: MutableList<Track>)
        fun deleteTrackFromPlaylist(id: Long)
        fun getCurrentTrack(): Track
        fun getNextTrack(): Track
        fun getNextTrackByClick(): Track
        fun getPrevTrack(): Track
        fun createAlbums()
        fun setCurrentPosition(position: Int) //???
        fun getCurrentPosition(): Int
        fun setLoopMode(mode: Int)
        fun getTrackById(id:Long): Track
        fun getAlbumsList(): MutableList<Album>
        fun setPlayingTrackList(trackList: MutableList<Track>)
    }

}