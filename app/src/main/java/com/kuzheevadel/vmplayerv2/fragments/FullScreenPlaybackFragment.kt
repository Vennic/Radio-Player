package com.kuzheevadel.vmplayerv2.fragments

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.SharedPreferences
import android.databinding.DataBindingUtil
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import com.kuzheevadel.vmplayerv2.bindhelper.BindServiceHelper
import com.kuzheevadel.vmplayerv2.R
import com.kuzheevadel.vmplayerv2.common.Constants
import com.kuzheevadel.vmplayerv2.common.DataBaseInfo
import com.kuzheevadel.vmplayerv2.common.LoadStateMessage
import com.kuzheevadel.vmplayerv2.common.Source
import com.kuzheevadel.vmplayerv2.dagger.App
import com.kuzheevadel.vmplayerv2.dagger.CustomViewModelFactory
import com.kuzheevadel.vmplayerv2.databinding.PlaybackLayoutBinding
import com.kuzheevadel.vmplayerv2.viewmodels.PlaybackViewModel
import kotlinx.android.synthetic.main.playback_controls_layout.view.*
import kotlinx.android.synthetic.main.top_playback_layout.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class FullScreenPlaybackFragment: Fragment() {

    @Inject
    lateinit var factory: CustomViewModelFactory
    @Inject
    lateinit var bindService: BindServiceHelper

    private lateinit var viewModel: PlaybackViewModel
    private lateinit var binding: PlaybackLayoutBinding
    private lateinit var progressData: MutableLiveData<Int>
    private lateinit var pref: SharedPreferences
    private var isTracksLoaded = false
    private var isServiceConnected = false
    private var isUpdated = false
    private var id: Long? = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)

        pref = activity!!.getSharedPreferences("laststate", Context.MODE_PRIVATE)

        (activity?.application as App).getComponent().inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(PlaybackViewModel::class.java)

        val callback = object : MediaControllerCompat.Callback() {
            override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                super.onPlaybackStateChanged(state)
                setPlaybackButtonImage(state)
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
                })

            }
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

            topPlaybackControls.top_play_pause_image_button.setOnClickListener {
                playOrPause()
            }

            playbackControlsContainer.playlist_image.setOnClickListener {
                viewModel.addOrDeleteTrackFromPlaylist()
            }

        }

        viewModel.trackData.observe(this, Observer {
            binding.updatePlaybackMessage = it
        })

        viewModel.trackIdData.observe(this, Observer {
            id = it
        })

        viewModel.dataBaseInfoData.observe(this, Observer {
            when (it) {
                DataBaseInfo.TRACK_ADDED -> {
                    binding.playbackControlsContainer.playlist_image.setImageResource(R.drawable.ic_playlist_add_black_24dp)
                    Toast.makeText(context, "Added in playlist", Toast.LENGTH_SHORT).show()
                    (binding.playbackControlsContainer.playlist_image.drawable as Animatable).start()
                }

                DataBaseInfo.ERROR -> Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()

                DataBaseInfo.DELETED -> {
                    binding.playbackControlsContainer.playlist_image.setImageResource(R.drawable.ic_playlist_delete_animatable)
                    Toast.makeText(context, "Deleted from playlist", Toast.LENGTH_SHORT).show()
                    (binding.playbackControlsContainer.playlist_image.drawable as Animatable).start()
                }

                DataBaseInfo.RADIO_ADDED -> {
                    binding.playbackControlsContainer.playlist_image.setImageResource(R.drawable.ic_unliked_to_liked)
                    (binding.playbackControlsContainer.playlist_image.drawable as Animatable).start()

                }

                DataBaseInfo.RADIO_IS_NOT_ADDED -> {
                    binding.playbackControlsContainer.playlist_image.setImageResource(R.drawable.ic_liked_to_unliked)
                    (binding.playbackControlsContainer.playlist_image.drawable as Animatable).start()
                }
            }
        })

        viewModel.checkPlaylistData.observe(this, Observer {

            when (it) {
                DataBaseInfo.TRACK_ADDED ->
                    binding.playbackControlsContainer.playlist_image.setImageResource(R.drawable.ic_playlist_delete_animatable)

                DataBaseInfo.TRACK_IS_NOT_ADDED ->
                    binding.playbackControlsContainer.playlist_image.setImageResource(R.drawable.ic_playlist_add_black_24dp)

                DataBaseInfo.RADIO_IS_NOT_ADDED ->
                    binding.playbackControlsContainer.playlist_image.setImageResource(R.drawable.ic_favorite_border_black_24dp)

                DataBaseInfo.RADIO_ADDED ->
                        binding.playbackControlsContainer.playlist_image.setImageResource(R.drawable.ic_favorite_black_24dp)
            }
        })


        return view
    }

    private fun setPlaybackButtonImage(state: PlaybackStateCompat?) {
        when (state?.state) {
            PlaybackStateCompat.STATE_PLAYING -> {
                binding.playbackControlsContainer.playback_play_pause_button.setImageResource(R.drawable.ic_play_to_pause)
                binding.topPlaybackControls.top_play_pause_image_button.setImageResource(R.drawable.ic_play_to_pause)
                (binding.topPlaybackControls.top_play_pause_image_button.drawable as Animatable).start()
                (binding.playbackControlsContainer.playback_play_pause_button.drawable as Animatable).start()
            }
            PlaybackStateCompat.STATE_PAUSED -> {
                binding.playbackControlsContainer.playback_play_pause_button.setImageResource(R.drawable.ic_pause_to_play)
                binding.topPlaybackControls.top_play_pause_image_button.setImageResource(R.drawable.ic_pause_to_play)
                (binding.topPlaybackControls.top_play_pause_image_button.drawable as Animatable).start()
                (binding.playbackControlsContainer.playback_play_pause_button.drawable as Animatable).start()
            }
        }
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

    override fun onPause() {
        super.onPause()
        val editor = pref.edit()
        editor.putLong(Constants.ID, id!!)
            .putInt(Constants.PROGRESS, progressData.value!!)
            .apply()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateUi(message: LoadStateMessage) {
        if (!isTracksLoaded) {
            isTracksLoaded = message.isTracksLoaded
        }

        if (!isServiceConnected) {
            isServiceConnected = message.isConnected
        }

        Log.i("PrefTest", message.toString())

        if (isTracksLoaded && isServiceConnected && !isUpdated) {
            isUpdated = true
            if (pref.contains(Constants.ID)) {
                val bundle = Bundle()
                val progress = pref.getInt(Constants.PROGRESS, 0)
                Log.i("PrefTest", progress.toString())

                bundle.putLong(Constants.ID, pref.getLong(Constants.ID, 0))

                bindService.mediaControllerCompat?.transportControls?.prepareFromMediaId(Constants.INIT, bundle)
                bindService.mediaControllerCompat?.transportControls?.seekTo((progress * 1000).toLong())
                progressData.value = progress
                binding.playbackControlsContainer.progress_seek_bar.progress = progress
            }
        }
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        bindService.unbindPlayerService()
        super.onDestroy()
    }

}