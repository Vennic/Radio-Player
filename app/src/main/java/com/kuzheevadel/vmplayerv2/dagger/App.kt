package com.kuzheevadel.vmplayerv2.dagger

import android.app.Application

class App: Application() {

    private lateinit var appComponent: AppComponent
    private var allTracksComponent: AllTracksComponent? = null

    companion object {
        lateinit var instance: App
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }

    fun createAllTracksComponent(): AllTracksComponent? {
        allTracksComponent = appComponent.getAllTracksComponent()
        return allTracksComponent
    }

    fun releaseAllTrackComponent() {
        allTracksComponent = null
    }
}