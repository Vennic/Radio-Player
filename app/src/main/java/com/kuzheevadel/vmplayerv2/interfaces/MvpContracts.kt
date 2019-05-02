package com.kuzheevadel.vmplayerv2.interfaces

import com.kuzheevadel.vmplayerv2.model.Track

class MvpContracts {
    interface PlaybackPresenter {

    }

    interface PlaybackView {

    }

    interface StorageMediaRepository {
        fun setTracksList(list: MutableList<Track>)
        fun getCurrentTrack(): Track
        fun getNextTrack(): Track
        fun getNextTrackByClick(): Track
        fun getPrevTrack(): Track
        fun setCurrentPosition(position: Int) //???
        fun getCurrentPosition(): Int
        fun setLoopMode(mode: Int)
        fun getAlbumsMap(): MutableMap<String, MutableList<Track>>
    }

    interface StorageMedia {
        fun getTracksList(): MutableList<Track>
    }
}