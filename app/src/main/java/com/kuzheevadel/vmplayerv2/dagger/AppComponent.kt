package com.kuzheevadel.vmplayerv2.dagger

import android.content.Context
import com.kuzheevadel.vmplayerv2.interfaces.MvpContracts
import com.kuzheevadel.vmplayerv2.model.Track
import com.kuzheevadel.vmplayerv2.repository.StorageMediaRepository
import com.kuzheevadel.vmplayerv2.services.StorageMedia
import dagger.Component
import dagger.Module
import dagger.Provides
import java.util.concurrent.Callable
import javax.inject.Scope

@Scope
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class PerApp

@Module
class AppModule(private val context: Context) {

    @PerApp
    @Provides
    fun provideContext(): Context {
        return context
    }

    @PerApp
    @Provides
    fun provideStorageMediaRepository(): MvpContracts.StorageMediaRepository {
        return StorageMediaRepository()
    }

    @Provides
    fun provideStorageMedia(context: Context): Callable<MutableList<Track>> {
        return StorageMedia(context)
    }
}

@PerApp
@Component(modules = [AppModule::class])
interface AppComponent {
    fun getAllTracksComponent(): AllTracksComponent
    fun getAlbumsComponent(): AlbumsComponent
}
