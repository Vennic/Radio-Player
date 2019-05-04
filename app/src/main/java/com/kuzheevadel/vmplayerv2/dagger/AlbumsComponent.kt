package com.kuzheevadel.vmplayerv2.dagger

import android.content.Context
import com.kuzheevadel.vmplayerv2.adapters.AlbumsAdapter
import com.kuzheevadel.vmplayerv2.fragments.AlbumsFragment
import com.kuzheevadel.vmplayerv2.interfaces.MvpContracts
import com.kuzheevadel.vmplayerv2.presenters.AlbumsFragPresenter
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Scope

@Scope
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class AlbumsScope

@Module
class AlbumsModule {

    @AlbumsScope
    @Provides
    fun providePresenter(mediaRepository: MvpContracts.StorageMediaRepository): MvpContracts.AlbumsPresenter {
        return AlbumsFragPresenter(mediaRepository)
    }

    @AlbumsScope
    @Provides
    fun provideAlbumsAdapter(context: Context): AlbumsAdapter {
        return AlbumsAdapter(context)
    }
}

@AlbumsScope
@Subcomponent(modules = [AlbumsModule::class])
interface AlbumsComponent {
    fun inject(albumsFragment: AlbumsFragment)
}