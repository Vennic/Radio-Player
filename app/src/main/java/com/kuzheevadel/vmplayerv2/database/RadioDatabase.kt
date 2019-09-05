package com.kuzheevadel.vmplayerv2.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.kuzheevadel.vmplayerv2.model.RadioStation

@Database(entities = [RadioStation::class], version = 1)
abstract class RadioDatabase: RoomDatabase() {
    abstract fun radioDao(): RadioDao
}