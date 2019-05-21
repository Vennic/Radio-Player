package com.kuzheevadel.vmplayerv2.fragments

import android.arch.lifecycle.ViewModelProviders
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.kuzheevadel.vmplayerv2.R
import com.kuzheevadel.vmplayerv2.dagger.App
import com.kuzheevadel.vmplayerv2.dagger.CustomViewModelFactory
import com.kuzheevadel.vmplayerv2.interfaces.Interfaces
import com.kuzheevadel.vmplayerv2.viewmodels.PlaybackViewModel
import kotlinx.android.synthetic.main.full_screen_playback.view.*
import javax.inject.Inject

class FullScreenPlaybackFragment: Fragment(), Interfaces.PlaybackView {

    @Inject
    lateinit var factory: CustomViewModelFactory

    lateinit var viewModel: PlaybackViewModel

    private var isPlaying = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as App).getComponent().inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(PlaybackViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.full_screen_playback, container, false)
        view.bottom_track_info_text.isSelected = true

        view.bottom_play_pause_image.setOnClickListener {
            Toast.makeText(context, "Play button", Toast.LENGTH_SHORT).show()
        }

        view.playback_play_pause_button.setOnClickListener {
            if (isPlaying) {
                view.playback_play_pause_button.setImageResource(R.drawable.ic_play_to_pause)
                (view.playback_play_pause_button.drawable as Animatable).start()
                isPlaying = false
            } else {
                view.playback_play_pause_button.setImageResource(R.drawable.ic_pause_to_play)
                (view.playback_play_pause_button.drawable as Animatable).start()
                isPlaying = true
            }
        }
        return view
    }

}