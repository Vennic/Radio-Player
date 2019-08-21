package com.kuzheevadel.vmplayerv2.services

import android.content.Context
import android.provider.MediaStore
import com.kuzheevadel.vmplayerv2.model.Track
import java.util.concurrent.Callable

class StorageMedia(private val context: Context): Callable<MutableList<Track>> {

    override fun call(): MutableList<Track> {
        return getTracksList()
    }

    private fun getTracksList(): MutableList<Track> {

        var count = 1L
        val tracksList = mutableListOf<Track>()
        val contentResolver = context.contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val cursor = contentResolver?.query(uri, null, null, null, null)

        if (cursor != null && cursor.moveToFirst()) {
            val titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val albumIdColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
            val durationColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
            val albumColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
            val data = cursor.getColumnIndex(MediaStore.Audio.Media.DATA)

            do {
                val id: Long = cursor.getLong(idColumn)
                val title: String = cursor.getString(titleColumn)
                val artist: String = cursor.getString(artistColumn)
                val albumId:Long = cursor.getLong(albumIdColumn)
                val duration: Int = cursor.getInt(durationColumn)
                val album: String = cursor.getString(albumColumn)
                val trackData: String = cursor.getString(data)

                tracksList.add(Track(id, title, artist, albumId, id, duration, album, count, false, trackData))
                count++

            } while (cursor.moveToNext())

            cursor.close()
        }
        return tracksList
    }
}