package com.kuzheevadel.vmplayerv2.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.kuzheevadel.vmplayerv2.R
import com.kuzheevadel.vmplayerv2.fragments.FullScreenPlaybackFragment
import kotlinx.android.synthetic.main.album_activity_layout.*

class AlbumActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.album_activity_layout)
        setSupportActionBar(player_album_toolbar)

        supportFragmentManager.beginTransaction()
            .replace(R.id.playback_container_detail, FullScreenPlaybackFragment(), null)
            .commit()
    }
}