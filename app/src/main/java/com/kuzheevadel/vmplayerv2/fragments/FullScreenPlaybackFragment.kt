package com.kuzheevadel.vmplayerv2.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.databinding.DataBindingUtil
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.support.v4.app.Fragment
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.kuzheevadel.vmplayerv2.R
import com.kuzheevadel.vmplayerv2.common.UpdateUIMessage
import com.kuzheevadel.vmplayerv2.dagger.App
import com.kuzheevadel.vmplayerv2.dagger.CustomViewModelFactory
import com.kuzheevadel.vmplayerv2.databinding.FullScreenPlaybackBinding
import com.kuzheevadel.vmplayerv2.interfaces.Interfaces
import com.kuzheevadel.vmplayerv2.model.Track
import com.kuzheevadel.vmplayerv2.services.PlayerService
import com.kuzheevadel.vmplayerv2.viewmodels.PlaybackViewModel
import kotlinx.android.synthetic.main.full_screen_playback.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class FullScreenPlaybackFragment: Fragment(), Interfaces.PlaybackView {

    @Inject
    lateinit var factory: CustomViewModelFactory

    lateinit var viewModel: PlaybackViewModel
    private lateinit var binding: FullScreenPlaybackBinding
    private lateinit var serviceConnection: ServiceConnection
    private var mediaControllerCompat: MediaControllerCompat? = null
    private var isPlaying = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as App).getComponent().inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(PlaybackViewModel::class.java)

        val callback = object : MediaControllerCompat.Callback() {
            override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                super.onPlaybackStateChanged(state)
            }
        }

        serviceConnection = object : ServiceConnection {

            override fun onServiceDisconnected(name: ComponentName?) {
                mediaControllerCompat = null
            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val serviceBinder = service as PlayerService.PlayerBinder

                try {
                    mediaControllerCompat = MediaControllerCompat(context, serviceBinder.getMediaSessionToken())
                    mediaControllerCompat?.registerCallback(callback)
                    callback.onPlaybackStateChanged(mediaControllerCompat?.playbackState)
                } catch (e: RemoteException) {
                    mediaControllerCompat = null
                }
            }
        }
        context?.bindService(Intent(context, PlayerService::class.java), serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.full_screen_playback, container, false)
        val view = binding.root

        binding.bottomTrackInfoText.isSelected = true

        binding.bottomPlayPauseImage.setOnClickListener {
            Toast.makeText(context, "Play button", Toast.LENGTH_SHORT).show()
        }
        binding.playbackPlayPauseButton.setOnClickListener {
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
        binding.nextTrack.setOnClickListener {
            mediaControllerCompat?.transportControls?.skipToNext()
        }

        binding.prevTrack.setOnClickListener {
            mediaControllerCompat?.transportControls?.skipToPrevious()
        }

        viewModel.trackData.observe(this, Observer {
            binding.playbackTrack = it
        })

        return view
    }

}