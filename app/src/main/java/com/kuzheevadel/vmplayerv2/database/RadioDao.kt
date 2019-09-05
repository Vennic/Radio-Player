package com.kuzheevadel.vmplayerv2.database

import android.arch.persistence.room.*
import com.kuzheevadel.vmplayerv2.model.RadioStation

@Dao
interface RadioDao {
    @Insert
    fun insertTrack(radioStation: RadioStation)

    @Delete
    fun deleteTrack(radioStation: RadioStation)

    @Update
    fun updateTrack(radioStation: RadioStation)

    @Query("SELECT * FROM radiostation")
    fun getAllTracks(): MutableList<RadioStation>

    @Query("SELECT * FROM radiostation WHERE id = :id")
    fun getRadiostationById(id: String): RadioStation
}