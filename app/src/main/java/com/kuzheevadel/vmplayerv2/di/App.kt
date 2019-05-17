package com.kuzheevadel.vmplayerv2.di

import android.app.Application
import android.content.Context
import com.kuzheevadel.vmplayerv2.activities.AlbumActivity
import com.kuzheevadel.vmplayerv2.adapters.AlbumsAdapter
import com.kuzheevadel.vmplayerv2.adapters.AlbumsTrackList
import com.kuzheevadel.vmplayerv2.adapters.TracksRecyclerAdapter
import com.kuzheevadel.vmplayerv2.fragments.AlbumsFragment
import com.kuzheevadel.vmplayerv2.fragments.AllTracksFragment
import com.kuzheevadel.vmplayerv2.fragments.FullScreenPlaybackFragment
import com.kuzheevadel.vmplayerv2.interfaces.Interfaces
import com.kuzheevadel.vmplayerv2.model.Track
import com.kuzheevadel.vmplayerv2.repository.StorageMediaRepository
import com.kuzheevadel.vmplayerv2.services.StorageMedia
import com.kuzheevadel.vmplayerv2.viewmodels.DetailAlbumViewModel
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import java.util.concurrent.Callable
import javax.inject.Singleton

class App: Application() {
    private val appComponent by lazy { DaggerApplicationComponent.builder()
        .application(this)
        .appModule(AppModule(this))
        .build()}

    fun getComponent(): ApplicationComponent {
        return appComponent
    }
}

@Singleton
@Component(modules = [AppModule::class])
interface ApplicationComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun appModule(appModule: AppModule): Builder

        fun build(): ApplicationComponent
    }

    fun inject(allTracksFragment: AllTracksFragment)
    fun inject(albumsFragment: AlbumsFragment)
    fun inject(playbackFragment: FullScreenPlaybackFragment)
    fun inject(detailAlbumViewModel: AlbumActivity)
}

@Module(includes = [Model::class])
class AppModule(val context: Context) {

    @Singleton
    @Provides
    fun provContext() = context

    @Provides
    fun provideDetailAlbumAdapter(context: Context): AlbumsTrackList {
        return AlbumsTrackList(context)
    }

    @Provides
    fun provideAlbumsAdapter(context: Context): AlbumsAdapter {
        return AlbumsAdapter(context)
    }

    @Provides
    fun provideAdapter(context: Context): TracksRecyclerAdapter {
        return TracksRecyclerAdapter(context)
    }

    @Singleton
    @Provides
    fun provideStorageMediaRepository(): Interfaces.StorageMediaRepository {
        return StorageMediaRepository()
    }

    @Provides
    fun provideStorageMedia(context: Context): Callable<MutableList<Track>> {
        return StorageMedia(context)
    }
}