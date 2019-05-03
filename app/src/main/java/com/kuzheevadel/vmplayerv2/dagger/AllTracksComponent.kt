package com.kuzheevadel.vmplayerv2.dagger

import android.content.Context
import com.kuzheevadel.vmplayerv2.fragments.AllTracksFragment
import com.kuzheevadel.vmplayerv2.interfaces.MvpContracts
import com.kuzheevadel.vmplayerv2.model.Track
import com.kuzheevadel.vmplayerv2.presenters.AllTracksPresenter
import com.kuzheevadel.vmplayerv2.services.StorageMedia
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import java.util.concurrent.Callable
import javax.inject.Scope

@Scope
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class AllTracksScope

@Module
class AllTracksModule {

    @AllTracksScope
    @Provides
    fun provideStorageMedia(context: Context): Callable<MutableList<Track>> {
        return StorageMedia(context)
    }

    @AllTracksScope
    @Provides
    fun providePresenter(storageMedia: Callable<MutableList<Track>>,
                         mediaRepository: MvpContracts.StorageMediaRepository): MvpContracts.AllTracksPresenter {
        return AllTracksPresenter(storageMedia, mediaRepository)
    }
}

@AllTracksScope
@Subcomponent(modules = [AllTracksModule::class])
interface AllTracksComponent{
    fun inject(allTracksFragment: AllTracksFragment)
}