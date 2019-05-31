package com.kuzheevadel.vmplayerv2.interfaces

import android.support.v4.app.FragmentManager
import com.kuzheevadel.vmplayerv2.model.Album
import com.kuzheevadel.vmplayerv2.model.Track
import com.kuzheevadel.vmplayerv2.radio.RadioStation
import io.reactivex.Observable

class Interfaces {

    interface Network {
        fun getStationsList(type: Int, page: Int): Observable<MutableList<RadioStation>>
    }

    interface AlbumsView {

    }

    interface AlbumsAdapter {
        fun updateAlbumsAdapter(list: MutableList<Album>)
        fun setFragmentManager(fm: FragmentManager)
    }

    interface TracksAdapter {
        fun updateTracksList(list: MutableList<Track>)
    }


    interface PlaybackView {

    }

    interface StorageMediaRepository {
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