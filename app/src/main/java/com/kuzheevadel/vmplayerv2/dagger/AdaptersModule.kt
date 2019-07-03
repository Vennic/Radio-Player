package com.kuzheevadel.vmplayerv2.dagger

import com.kuzheevadel.vmplayerv2.adapters.*
import com.kuzheevadel.vmplayerv2.helper.BindServiceHelper
import com.kuzheevadel.vmplayerv2.interfaces.Interfaces
import com.kuzheevadel.vmplayerv2.paging.RadioPagingAdapter
import com.kuzheevadel.vmplayerv2.repository.RadioRepository
import dagger.Module
import dagger.Provides


@Module
class AdaptersModule {

    @Provides
    fun provideDetailAlbumAdapter(mediaRepository: Interfaces.StorageMediaRepository, bindServiceHelper: BindServiceHelper): AlbumsTracksListAdapter {
        return AlbumsTracksListAdapter(mediaRepository, bindServiceHelper)
    }

    @Provides
    fun provideAlbumsAdapter(): AlbumsListAdapter {
        return AlbumsListAdapter()
    }

    @Provides
    fun provideRadioAdapter(bindServiceHelper: BindServiceHelper, radioRepository: RadioRepository): RadioStationsAdapter {
        return RadioStationsAdapter(bindServiceHelper, radioRepository)
    }

    @Provides
    fun provideTrackListAdapter(mediaRepository: Interfaces.StorageMediaRepository, bindServiceHelper: BindServiceHelper): TrackListAdapter {
        return TrackListAdapter(mediaRepository, bindServiceHelper)
    }

    @Provides
    fun provideSearchPagingAdapter(bindServiceHelper: BindServiceHelper, radioRepository: RadioRepository): RadioPagingAdapter {
        return RadioPagingAdapter(bindServiceHelper, radioRepository)
    }

    @Provides
    fun providePlaylistAdapter(mediaRepository: Interfaces.StorageMediaRepository, bindServiceHelper: BindServiceHelper): PlaylistAdapter {
        return PlaylistAdapter(mediaRepository, bindServiceHelper)
    }

}