package com.kuzheevadel.vmplayerv2.interfaces

import com.kuzheevadel.vmplayerv2.model.Album
import com.kuzheevadel.vmplayerv2.model.Track
import io.reactivex.Observable

class MvpContracts {

    interface TracksAdapter {
        fun updateTracksList(list: MutableList<Track>)
    }

    interface AllTracksPresenter {
        fun updateAdapter()
        fun setAdapter(adapter: MvpContracts.TracksAdapter)
        fun loadTracks()
    }
    interface PlaybackPresenter {

    }

    interface PlaybackView {

    }

    interface StorageMediaRepository {
        fun getTracksList(): MutableList<Track>
        fun setTracksList(list: MutableList<Track>)
        fun getCurrentTrack(): Track
        fun getNextTrack(): Track
        fun getNextTrackByClick(): Track
        fun getPrevTrack(): Track
        fun setCurrentPosition(position: Int) //???
        fun getCurrentPosition(): Int
        fun setLoopMode(mode: Int)
        fun getAlbumsList(): MutableList<Album>
    }

}