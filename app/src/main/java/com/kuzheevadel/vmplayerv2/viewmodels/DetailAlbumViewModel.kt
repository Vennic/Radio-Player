package com.kuzheevadel.vmplayerv2.viewmodels

import android.arch.lifecycle.ViewModel
import com.kuzheevadel.vmplayerv2.adapters.AlbumsTrackList
import com.kuzheevadel.vmplayerv2.interfaces.Interfaces
import com.kuzheevadel.vmplayerv2.model.Track
import javax.inject.Inject

class DetailAlbumViewModel @Inject constructor(private val mediaRepository: Interfaces.StorageMediaRepository): ViewModel() {
    private lateinit var mAdapter: AlbumsTrackList
    private lateinit var trackList: MutableList<Track>

    fun setAdapter(adapter: AlbumsTrackList) {
        mAdapter = adapter
    }

    fun setAlbum(pos: Int) {
        trackList = mediaRepository.getAlbumsList()[pos].tracksList
        mAdapter.setList(trackList)
    }


}