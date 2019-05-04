package com.kuzheevadel.vmplayerv2.presenters

import com.kuzheevadel.vmplayerv2.interfaces.MvpContracts

class AlbumsFragPresenter(private val mediaRepository: MvpContracts.StorageMediaRepository): MvpContracts.AlbumsPresenter {
    private lateinit var mAdapter: MvpContracts.AlbumsAdapter

    override fun setAdapter(adapter: MvpContracts.AlbumsAdapter) {
        mAdapter = adapter
    }

    override fun updateAdapter() {
        mAdapter.updateAlbumsAdapter(mediaRepository.getAlbumsList())
    }
}