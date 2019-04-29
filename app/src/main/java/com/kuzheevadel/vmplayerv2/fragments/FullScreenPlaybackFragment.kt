package com.kuzheevadel.vmplayerv2.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.kuzheevadel.vmplayerv2.R
import kotlinx.android.synthetic.main.full_screen_playback.view.*

class FullScreenPlaybackFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.full_screen_playback, container, false)
        view.bottom_track_info_text.isSelected = true

        view.bottom_play_pause_image.setOnClickListener {
            Toast.makeText(context, "Play button", Toast.LENGTH_SHORT).show()
        }

        return view
    }

}