package com.kuzheevadel.vmplayerv2.fragments

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.kuzheevadel.vmplayerv2.bindhelper.BindServiceHelper
import com.kuzheevadel.vmplayerv2.R
import com.kuzheevadel.vmplayerv2.common.Source
import com.kuzheevadel.vmplayerv2.dagger.App
import com.kuzheevadel.vmplayerv2.dagger.CustomViewModelFactory
import com.kuzheevadel.vmplayerv2.databinding.PlaybackLayoutBinding
import com.kuzheevadel.vmplayerv2.viewmodels.PlaybackViewModel
import kotlinx.android.synthetic.main.playback_controls_layout.view.*
import kotlinx.android.synthetic.main.top_playback_layout.view.*
import javax.inject.Inject

class FullScreenPlaybackFragment: Fragment() {

    @Inject
    lateinit var factory: CustomViewModelFactory
    @Inject
    lateinit var bindService: BindServiceHelper

    private lateinit var viewModel: PlaybackViewModel
    private lateinit var binding: PlaybackLayoutBinding
    private lateinit var progressData: MutableLiveData<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as App).getComponent().inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(PlaybackViewModel::class.java)

        val callback = object : MediaControllerCompat.Callback() {
            override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                super.onPlaybackStateChanged(state)
                when (state?.state) {
                    PlaybackStateCompat.STATE_PLAYING -> {
                        binding.playbackControlsContainer.playback_play_pause_button.setImageResource(R.drawable.ic_play_to_pause)
                        (binding.playbackControlsContainer.playback_play_pause_button.drawable as Animatable).start()
                    }
                    PlaybackStateCompat.STATE_PAUSED -> {
                        binding.playbackControlsContainer.playback_play_pause_button.setImageResource(R.drawable.ic_pause_to_play)
                        (binding.playbackControlsContainer.playback_play_pause_button.drawable as Animatable).start()
                    }
                }
            }
        }



        bindService.setOnConnectionListener(object : BindServiceHelper.OnConnectionListener {
            override fun setProgressData(data: MutableLiveData<Int>, source: Source) {
                progressData = data
                viewModel.source = source
                viewModel.initViewModel()

                progressData.observe(this@FullScreenPlaybackFragment, Observer {
                    binding.playbackControlsContainer.current_duration_text.text = getDurationInTimeFormat(it!!)
                    binding.playbackControlsContainer.progress_seek_bar.progress = it
                }) }
        })

        bindService.bindPlayerService(callback)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.playback_layout, container, false)
        val view = binding.root

        binding.apply {
            topPlaybackControls.bottom_track_info_text.isSelected = true

            playbackControlsContainer.playback_play_pause_button.setOnClickListener {
                playOrPause()
            }

            playbackControlsContainer.progress_seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    bindService.mediaControllerCompat?.transportControls?.seekTo((seekBar.progress * 1000).toLong())
                }

            })

            playbackControlsContainer.next_track.setOnClickListener {
                playbackControlsContainer.progress_seek_bar.progress = 0
                bindService.mediaControllerCompat?.transportControls?.skipToNext()
            }

            playbackControlsContainer.prev_track.setOnClickListener {
                playbackControlsContainer.progress_seek_bar.progress = 0
                bindService.mediaControllerCompat?.transportControls?.skipToPrevious()
            }

            playbackControlsContainer.shuffle_image_button.setOnClickListener {
                viewModel.addTrackToPlaylistDatabase()
            }

            playbackControlsContainer.playlist_image.setOnClickListener {
                (playbackControlsContainer.playlist_image.drawable as Animatable).start()
            }

        }

        viewModel.trackData.observe(this, Observer {
            binding.updatePlaybackMessage = it
        })

        return view
    }

    private fun playOrPause() {
        with(bindService.mediaControllerCompat) {
            if (this?.playbackState?.state == PlaybackStateCompat.STATE_PLAYING) {
                transportControls.pause()
            } else {
                this?.transportControls?.play()
            }
        }
    }

    fun getDurationInTimeFormat(time: Int): String {
        val minutes = time / 60
        val seconds = time % 60
        return "$minutes:${if (seconds < 10) "0" else ""}$seconds"
    }

    override fun onDestroy() {
        bindService.unbindPlayerService()
        super.onDestroy()
    }

}