package com.kuzheevadel.vmplayerv2.dagger

import android.content.Context
import dagger.Component
import dagger.Module
import javax.inject.Scope

@Scope
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class PerApp

@Module
class AppModule(private val context: Context) {

    @PerApp
    fun provideContext(): Context {
        return context
    }
}

@PerApp
@Component(modules = [AppModule::class])
interface AppComponent {
    fun getAllTracksComponent(): AllTracksComponent
}
