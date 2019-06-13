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
import com.kuzheevadel.vmplayerv2.dagger.App
import com.kuzheevadel.vmplayerv2.dagger.CustomViewModelFactory
import com.kuzheevadel.vmplayerv2.databinding.FullScreenPlaybackBinding
import com.kuzheevadel.vmplayerv2.viewmodels.PlaybackViewModel
import javax.inject.Inject

class FullScreenPlaybackFragment: Fragment() {

    @Inject
    lateinit var factory: CustomViewModelFactory
    @Inject
    lateinit var bindService: BindServiceHelper

    private lateinit var viewModel: PlaybackViewModel
    private lateinit var binding: FullScreenPlaybackBinding
    private lateinit var progressData: MutableLiveData<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as App).getComponent().inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(PlaybackViewModel::class.java)

        val callback = object : MediaControllerCompat.Callback() {
            /*override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                super.onPlaybackStateChanged(state)
            }*/
        }



        bindService.setOnConnectionListener(object : BindServiceHelper.OnConnectionListener {
            override fun setProgressData(data: MutableLiveData<Int>) {
                progressData = data
                progressData.observe(this@FullScreenPlaybackFragment, Observer {
                    binding.currentDurationText.text = getDurationInTimeFormat(it!!)
                    binding.progressSeekBar.progress = it
                }) }
        })

        bindService.bindPlayerService(callback)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.full_screen_playback, container, false)
        val view = binding.root

        binding.apply {
            bottomTrackInfoText.isSelected = true

            playbackPlayPauseButton.setOnClickListener {
                playOrPause()
            }

            progressSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    bindService.mediaControllerCompat?.transportControls?.seekTo((seekBar.progress * 1000).toLong())
                }

            })

            nextTrack.setOnClickListener {
                progressSeekBar.progress = 0
                bindService.mediaControllerCompat?.transportControls?.skipToNext()
            }

            prevTrack.setOnClickListener {
                progressSeekBar.progress = 0
                bindService.mediaControllerCompat?.transportControls?.skipToPrevious()
            }

            shuffleImageButton.setOnClickListener {
                viewModel.addTrackToPlaylistDatabase()
            }

        }

        viewModel.trackData.observe(this, Observer {
            binding.playbackTrack = it
        })

        return view
    }

    private fun playOrPause() {
        with(bindService.mediaControllerCompat) {
            if (this?.playbackState?.state == PlaybackStateCompat.STATE_PLAYING) {
                transportControls.pause()
                binding.playbackPlayPauseButton.setImageResource(R.drawable.ic_pause_to_play)
                (binding.playbackPlayPauseButton.drawable as Animatable).start()
            } else {
                this?.transportControls?.play()
                binding.playbackPlayPauseButton.setImageResource(R.drawable.ic_play_to_pause)
                (binding.playbackPlayPauseButton.drawable as Animatable).start()
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