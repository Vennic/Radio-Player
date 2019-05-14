package com.kuzheevadel.vmplayerv2.viewmodels

import android.arch.lifecycle.ViewModel
import com.kuzheevadel.vmplayerv2.interfaces.Interfaces
import javax.inject.Inject

class AlbumViewModel @Inject constructor(private val mediaRepository: Interfaces.StorageMediaRepository): ViewModel() {

    private lateinit var mAdapter: Interfaces.AlbumsAdapter

    fun setAdapter(adapter: Interfaces.AlbumsAdapter) {
        mAdapter = adapter
    }

    fun updateAdapter() {
        mAdapter.updateAlbumsAdapter(mediaRepository.getAlbumsList())
    }
}