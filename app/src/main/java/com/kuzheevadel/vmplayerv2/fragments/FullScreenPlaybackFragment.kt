package com.kuzheevadel.vmplayerv2.fragments

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.SharedPreferences
import android.content.res.TypedArray
import android.databinding.DataBindingUtil
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.Toast
import com.kuzheevadel.vmplayerv2.R
import com.kuzheevadel.vmplayerv2.common.Constants
import com.kuzheevadel.vmplayerv2.common.DataBaseInfo
import com.kuzheevadel.vmplayerv2.common.Source
import com.kuzheevadel.vmplayerv2.dagger.App
import com.kuzheevadel.vmplayerv2.dagger.CustomViewModelFactory
import com.kuzheevadel.vmplayerv2.databinding.PlaybackLayoutBinding
import com.kuzheevadel.vmplayerv2.helper.BindServiceHelper
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
    private lateinit var pref: SharedPreferences
    private  var mContext: Context? = null
    private var currentShuffleMode = Constants.SHUFFLE_MODE_OFF
    private var currentRepeatMode = Constants.NO_LOOP_MODE
    private lateinit var shuffleImageButton: ImageButton
    private lateinit var repeatImageButton: ImageButton
    private lateinit var callback:  MediaControllerCompat.Callback
    private lateinit var app: App

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context
    }

    private fun connectToPlayerService() {
        callback = object : MediaControllerCompat.Callback() {
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
                    binding.topPlaybackControls.top_playback_progress.progress = it
                })
            }
        })

        bindService.bindPlayerService(callback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pref = activity!!.getSharedPreferences("laststate", Context.MODE_PRIVATE)

        app = (activity?.application as App)
        app.getComponent().inject(this)

        viewModel = ViewModelProviders.of(this, factory).get(PlaybackViewModel::class.java)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.playback_layout, container, false)


        val view = binding.root
        shuffleImageButton = binding.playbackControlsContainer.shuffle_image_button
        repeatImageButton = binding.playbackControlsContainer.loop_image_button

        binding.apply {
            topPlaybackControls.bottom_track_info_text.isSelected = true
            playbackControlsContainer.playback_track_name.isSelected = true
            playbackControlsContainer.playback_artist_name.isSelected = true

            with(playbackControlsContainer) {
                next_track.setOnClickListener {
                    playbackControlsContainer.progress_seek_bar.progress = 0
                    bindService.mediaControllerCompat?.transportControls?.skipToNext()
                }

                prev_track.setOnClickListener {
                    playbackControlsContainer.progress_seek_bar.progress = 0
                    bindService.mediaControllerCompat?.transportControls?.skipToPrevious()
                }

                playlist_image.setOnClickListener {
                    viewModel.addOrDeleteTrackFromPlaylist()
                }

                progress_seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar) {
                        bindService.mediaControllerCompat?.transportControls?.seekTo((seekBar.progress * 1000).toLong())
                        binding.topPlaybackControls.top_playback_progress.progress = seekBar.progress
                    }

                })

                playback_play_pause_button.setOnClickListener {
                    playOrPause()
                }

                shuffleImageButton.setOnClickListener {
                    setShuffleMode(it as ImageButton)
                }

                repeatImageButton.setOnClickListener {
                    setRepeatMode(it as ImageButton)
                }

            }

            topPlaybackControls.top_play_pause_image_button.setOnClickListener {
                playOrPause()
            }
        }

        viewModel.trackData.observe(this, Observer {

            binding.updatePlaybackMessage = it
            setPlaybackButtonsState(it!!.type)
        })

        viewModel.dataBaseInfoData.observe(this, Observer {
            when (it) {
                DataBaseInfo.TRACK_ADDED -> {
                    binding.playbackControlsContainer.playlist_image.setImageResource(getStyleableDrawable(R.attr.playlistAddAnimatable))
                    Toast.makeText(mContext, getString(R.string.added_in_playlist), Toast.LENGTH_SHORT).show()
                    (binding.playbackControlsContainer.playlist_image.drawable as Animatable).start()
                }

                DataBaseInfo.ERROR -> Toast.makeText(context, getString(R.string.error_add), Toast.LENGTH_SHORT).show()

                DataBaseInfo.DELETED -> {
                    binding.playbackControlsContainer.playlist_image.setImageResource(getStyleableDrawable(R.attr.playlistDeleteAnimatable))
                    Toast.makeText(mContext, getString(R.string.deleted_from_playlist), Toast.LENGTH_SHORT).show()
                    (binding.playbackControlsContainer.playlist_image.drawable as Animatable).start()
                }

                DataBaseInfo.RADIO_ADDED -> {
                    binding.playbackControlsContainer.playlist_image.setImageResource(getStyleableDrawable(R.attr.fromUnlikedToLiked))
                    (binding.playbackControlsContainer.playlist_image.drawable as Animatable).start()

                }

                DataBaseInfo.RADIO_IS_NOT_ADDED -> {
                    binding.playbackControlsContainer.playlist_image.setImageResource(getStyleableDrawable(R.attr.fromLikedToUnliked))
                    (binding.playbackControlsContainer.playlist_image.drawable as Animatable).start()
                }
            }
        })

        viewModel.checkPlaylistData.observe(this, Observer {

            when (it) {
                DataBaseInfo.TRACK_ADDED ->
                    binding.playbackControlsContainer.playlist_image.setImageResource(getStyleableDrawable(R.attr.playlistDeleteAnimatable))

                DataBaseInfo.TRACK_IS_NOT_ADDED ->
                    binding.playbackControlsContainer.playlist_image.setImageResource(getStyleableDrawable(R.attr.playlistAddAnimatable))

                DataBaseInfo.RADIO_IS_NOT_ADDED ->
                    binding.playbackControlsContainer.playlist_image.setImageResource(getStyleableDrawable(R.attr.unLikedImage))

                DataBaseInfo.RADIO_ADDED ->
                        binding.playbackControlsContainer.playlist_image.setImageResource(R.drawable.ic_favorite_red_24dp)
            }
        })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        connectToPlayerService()
    }

    private fun setShuffleMode(button: ImageButton) {
        when (currentShuffleMode) {
            Constants.SHUFFLE_MODE_OFF -> {
                button.setImageResource(getStyleableDrawable(R.attr.shuffleButton))
                bindService.mediaControllerCompat?.transportControls?.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_ALL)
                currentShuffleMode = Constants.SHUFFLE_MODE_ON
            }

            Constants.SHUFFLE_MODE_ON -> {
                button.setImageResource(R.drawable.ic_shuffle_disabled)
                bindService.mediaControllerCompat?.transportControls?.setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_NONE)
                currentShuffleMode = Constants.SHUFFLE_MODE_OFF
            }
        }
    }

    private fun setRepeatMode(button: ImageButton) {
        when (currentRepeatMode) {
            Constants.NO_LOOP_MODE -> {
                button.setImageResource(getStyleableDrawable(R.attr.oneLoopModeButton))
                bindService.mediaControllerCompat?.transportControls?.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ONE)
                currentRepeatMode = Constants.ONE_LOOP_MODE
            }

            Constants.ONE_LOOP_MODE -> {
                button.setImageResource(getStyleableDrawable(R.attr.loopModeButton))
                bindService.mediaControllerCompat?.transportControls?.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL)
                currentRepeatMode = Constants.ALL_LOOP_MODE
            }

            Constants.ALL_LOOP_MODE -> {
                button.setImageResource(R.drawable.ic_repeat_disable)
                bindService.mediaControllerCompat?.transportControls?.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_NONE)
                currentRepeatMode = Constants.NO_LOOP_MODE
            }
        }
    }

    private fun setPlaybackButtonsState(source: Source) {
        when (source) {
            Source.RADIO -> {
                binding.playbackControlsContainer.run {
                    prev_track.visibility = View.INVISIBLE
                    next_track.visibility = View.INVISIBLE
                    shuffle_image_button.setImageResource(R.drawable.ic_shuffle_disabled)
                    shuffle_image_button.isEnabled = false
                    loop_image_button.setImageResource(R.drawable.ic_repeat_disable)
                    loop_image_button.isEnabled = false
                }
            }

            Source.TRACK -> {
                binding.playbackControlsContainer.run {
                    prev_track.visibility = View.VISIBLE
                    next_track.visibility = View.VISIBLE
                    loop_image_button.isEnabled = true
                    shuffle_image_button.isEnabled = true

                    when (currentRepeatMode) {
                        Constants.NO_LOOP_MODE -> repeatImageButton.setImageResource(R.drawable.ic_repeat_disable)
                        Constants.ONE_LOOP_MODE -> repeatImageButton.setImageResource(getStyleableDrawable(R.attr.oneLoopModeButton))
                        Constants.ALL_LOOP_MODE -> repeatImageButton.setImageResource(getStyleableDrawable(R.attr.loopModeButton))
                    }

                    when (currentShuffleMode) {
                        Constants.SHUFFLE_MODE_ON -> shuffleImageButton.setImageResource(getStyleableDrawable(R.attr.shuffleButton))
                        Constants.SHUFFLE_MODE_OFF -> shuffleImageButton.setImageResource(R.drawable.ic_shuffle_disabled)
                    }
                }
            }
        }
    }

    private fun setPlaybackButtonImage(state: PlaybackStateCompat?) {
        when (state?.state) {
            PlaybackStateCompat.STATE_PLAYING -> {
                binding.playbackControlsContainer.playback_play_pause_button.setImageResource(getStyleableDrawable(R.attr.playToPauseAnimate))
                binding.topPlaybackControls.top_play_pause_image_button.setImageResource(getStyleableDrawable(R.attr.playToPauseAnimate))
                (binding.topPlaybackControls.top_play_pause_image_button.drawable as Animatable).start()
                (binding.playbackControlsContainer.playback_play_pause_button.drawable as Animatable).start()
            }
            PlaybackStateCompat.STATE_PAUSED -> {
                binding.playbackControlsContainer.playback_play_pause_button.setImageResource(getStyleableDrawable(R.attr.pauseToPlayAnimate))
                binding.topPlaybackControls.top_play_pause_image_button.setImageResource(getStyleableDrawable(R.attr.pauseToPlayAnimate))
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

    private fun getStyleableDrawable(attribute: Int): Int {
        val a: TypedArray? = mContext?.theme?.obtainStyledAttributes(intArrayOf(attribute))
        return a!!.getResourceId(0, -1)
    }

    override fun onDestroy() {
        bindService.unbindPlayerService()
        super.onDestroy()
    }

}