package com.kuzheevadel.vmplayerv2.dagger

import com.kuzheevadel.vmplayerv2.interfaces.Interfaces
import com.kuzheevadel.vmplayerv2.repository.RadioRepository
import com.kuzheevadel.vmplayerv2.repository.StorageMediaRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoriesModule {

    @Singleton
    @Provides
    fun provideStorageMediaRepository(): Interfaces.StorageMediaRepository {
        return StorageMediaRepository()
    }

    @Singleton
    @Provides
    fun provideRadioReoisitory(): RadioRepository {
        return RadioRepository()
    }
}