package com.kuzheevadel.vmplayerv2.viewmodels

import android.arch.lifecycle.ViewModel
import com.kuzheevadel.vmplayerv2.adapters.AlbumsListAdapter
import com.kuzheevadel.vmplayerv2.interfaces.Interfaces
import javax.inject.Inject

class AlbumViewModel @Inject constructor(private val mediaRepository: Interfaces.StorageMediaRepository): ViewModel() {

    private lateinit var mAdapter: AlbumsListAdapter

    fun setAdapter(adapter: AlbumsListAdapter) {
        mAdapter = adapter
    }

    fun updateAdapter() {
        mAdapter.albumsList = mediaRepository.getAlbumsList()
        mAdapter.notifyDataSetChanged()
    }

}