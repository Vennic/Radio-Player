package com.kuzheevadel.vmplayerv2.dagger

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import android.support.v4.app.Fragment
import com.kuzheevadel.vmplayerv2.Helpers.BindServiceHelper
import com.kuzheevadel.vmplayerv2.activities.AlbumActivity
import com.kuzheevadel.vmplayerv2.adapters.AlbumsListAdapter
import com.kuzheevadel.vmplayerv2.adapters.AlbumsTracksListAdapter
import com.kuzheevadel.vmplayerv2.adapters.RadioStationsAdapter
import com.kuzheevadel.vmplayerv2.adapters.TrackListAdapter
import com.kuzheevadel.vmplayerv2.database.PlaylistDatabase
import com.kuzheevadel.vmplayerv2.fragments.*
import com.kuzheevadel.vmplayerv2.interfaces.Interfaces
import com.kuzheevadel.vmplayerv2.model.Track
import com.kuzheevadel.vmplayerv2.repository.StorageMediaRepository
import com.kuzheevadel.vmplayerv2.services.PlayerService
import com.kuzheevadel.vmplayerv2.services.StorageMedia
import com.kuzheevadel.vmplayerv2.services.VmpNetwork
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

    fun inject(fragment: AllTracksFragment)
    fun inject(fragment: AlbumsFragment)
    fun inject(fragment: FullScreenPlaybackFragment)
    fun inject(fragment: AlbumActivity)
    fun inject(fragment: RadioFragment)
    fun inject(service: PlayerService)
    fun inject(fragment: PopularRadioFragment)
    fun inject(fragment: PlaylistFragment)
    fun inject(fragment: SearchRadioFragment)

}

@Module(includes = [Model::class])
class AppModule(val context: Context) {

    @Singleton
    @Provides
    fun provContext() = context

    @Provides
    fun provideBindHelper(context: Context): BindServiceHelper = BindServiceHelper(context)

    @Provides
    fun provideDetailAlbumAdapter(mediaRepository: Interfaces.StorageMediaRepository, bindServiceHelper: BindServiceHelper): AlbumsTracksListAdapter {
        return AlbumsTracksListAdapter(mediaRepository, bindServiceHelper)
    }

    @Provides
    fun provideAlbumsAdapter(): AlbumsListAdapter {
        return AlbumsListAdapter()
    }

    @Provides
    fun provideRadioAdapter(bindServiceHelper: BindServiceHelper): RadioStationsAdapter {
        return RadioStationsAdapter(bindServiceHelper)
    }

    @Singleton
    @Provides
    fun provideDatabase(context: Context): PlaylistDatabase {
        return Room.databaseBuilder(context, PlaylistDatabase::class.java, "playlistDatabase").build()
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

    @Provides
    fun provideTrackListAdapter(mediaRepository: Interfaces.StorageMediaRepository, bindServiceHelper: BindServiceHelper): TrackListAdapter {
        return TrackListAdapter(mediaRepository, bindServiceHelper)
    }

    @Singleton
    @Provides
    fun provideVmpNetwork(): Interfaces.Network {
        return VmpNetwork()
    }

    @Singleton
    @Provides
    fun provideNetwork(): VmpNetwork {
        return VmpNetwork()
    }
}