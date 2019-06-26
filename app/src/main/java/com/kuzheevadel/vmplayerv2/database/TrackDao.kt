package com.kuzheevadel.vmplayerv2.database

import android.arch.persistence.room.*
import com.kuzheevadel.vmplayerv2.model.Track

@Dao
interface TrackDao {

    @Insert
    fun insertTrack(track: Track)

    @Delete
    fun deleteTrack(track: Track)

    @Update
    fun updateTrack(track: Track)

    @Query("SELECT * FROM track")
    fun getAllTracks(): MutableList<Track>

    @Query("SELECT * FROM track WHERE id = :id")
    fun getTrackById(id: Long): Track
}