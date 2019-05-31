package com.kuzheevadel.vmplayerv2.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

import com.kuzheevadel.vmplayerv2.model.Track

@Database(entities = [Track::class], version = 1)
abstract class PlaylistDatabase: RoomDatabase() {
    abstract fun trackDao(): TrackDao
}