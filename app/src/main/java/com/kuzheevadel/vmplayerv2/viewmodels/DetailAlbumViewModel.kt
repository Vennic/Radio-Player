package com.kuzheevadel.vmplayerv2.viewmodels

import android.arch.lifecycle.ViewModel
import com.kuzheevadel.vmplayerv2.adapters.AlbumsTracksListAdapter
import com.kuzheevadel.vmplayerv2.interfaces.Interfaces
import com.kuzheevadel.vmplayerv2.model.Track
import javax.inject.Inject

class DetailAlbumViewModel @Inject constructor(private val mediaRepository: Interfaces.StorageMediaRepository): ViewModel() {
    private lateinit var mAdapter: AlbumsTracksListAdapter
    private lateinit var trackList: MutableList<Track>

    fun setAdapter(adapter: AlbumsTracksListAdapter) {
        mAdapter = adapter
    }

    fun setAlbum(pos: Int) {
        trackList = mediaRepository.getAlbumsList()[pos].tracksList
        mAdapter.trackList = trackList
        mAdapter.notifyDataSetChanged()
    }


}