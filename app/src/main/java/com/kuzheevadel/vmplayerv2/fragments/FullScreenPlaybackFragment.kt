package com.kuzheevadel.vmplayerv2.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.media.session.MediaControllerCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kuzheevadel.vmplayerv2.Helpers.BindServiceHelper
import com.kuzheevadel.vmplayerv2.R
import com.kuzheevadel.vmplayerv2.dagger.App
import com.kuzheevadel.vmplayerv2.dagger.CustomViewModelFactory
import com.kuzheevadel.vmplayerv2.databinding.FullScreenPlaybackBinding
import com.kuzheevadel.vmplayerv2.interfaces.Interfaces
import com.kuzheevadel.vmplayerv2.viewmodels.PlaybackViewModel
import kotlinx.android.synthetic.main.full_screen_playback.view.*
import javax.inject.Inject

class FullScreenPlaybackFragment: Fragment(), Interfaces.PlaybackView {

    @Inject
    lateinit var factory: CustomViewModelFactory
    @Inject
    lateinit var bindService: BindServiceHelper

    private lateinit var viewModel: PlaybackViewModel
    private lateinit var binding: FullScreenPlaybackBinding
    private var isPlaying = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as App).getComponent().inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(PlaybackViewModel::class.java)

        val callback = object : MediaControllerCompat.Callback() {
            /*override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                super.onPlaybackStateChanged(state)
            }*/
        }

        bindService.bindPlayerService(callback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.full_screen_playback, container, false)
        val view = binding.root

        binding.apply {
            bottomTrackInfoText.isSelected = true

            playbackPlayPauseButton.setOnClickListener {
                isPlaying = if (isPlaying) {
                    view.playback_play_pause_button.setImageResource(R.drawable.ic_play_to_pause)
                    (view.playback_play_pause_button.drawable as Animatable).start()
                    false
                } else {
                    view.playback_play_pause_button.setImageResource(R.drawable.ic_pause_to_play)
                    (view.playback_play_pause_button.drawable as Animatable).start()
                    true
                }
            }

            nextTrack.setOnClickListener {
                bindService.mediaControllerCompat?.transportControls?.skipToNext()
            }

            prevTrack.setOnClickListener {
                bindService.mediaControllerCompat?.transportControls?.skipToPrevious()
            }

            shuffleImage.setOnClickListener {

            }
        }

        viewModel.trackData.observe(this, Observer {
            binding.playbackTrack = it
        })

        return view
    }

    override fun onDestroy() {
        bindService.unbindPlayerService()
        super.onDestroy()
    }

}