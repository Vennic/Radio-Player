package com.kuzheevadel.vmplayerv2.services

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaButtonReceiver
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.kuzheevadel.vmplayerv2.activities.PlayerActivity
import com.kuzheevadel.vmplayerv2.common.Constants
import com.kuzheevadel.vmplayerv2.dagger.App
import com.kuzheevadel.vmplayerv2.interfaces.Interfaces
import com.kuzheevadel.vmplayerv2.model.Track
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class PlayerService: Service() {

    @Inject
    lateinit var mediaRepository: Interfaces.StorageMediaRepository

    private lateinit var mExoplayer: SimpleExoPlayer
    private val metadataBuilder: MediaMetadataCompat.Builder = MediaMetadataCompat.Builder()
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var audioFocusRequest: AudioFocusRequest
    private var isAudioFocusRequest = false
    private lateinit var audioManager: AudioManager

    private val stateBuilder = PlaybackStateCompat.Builder().setActions(
        PlaybackStateCompat.ACTION_PLAY
                or PlaybackStateCompat.ACTION_STOP
                or PlaybackStateCompat.ACTION_PAUSE
                or PlaybackStateCompat.ACTION_PLAY_PAUSE
                or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
    )

    override fun onCreate() {
        super.onCreate()
        (application as App).getComponent().inject(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()

            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setOnAudioFocusChangeListener(audioFocusChangeListener)
                .setAcceptsDelayedFocusGain(false)
                .setWillPauseWhenDucked(true)
                .setAudioAttributes(audioAttributes)
                .build()
        }

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mediaSession = MediaSessionCompat(this, "VMPlayer")

        val activityIntent = Intent(applicationContext, PlayerActivity::class.java)
        val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON, null, applicationContext, MediaButtonReceiver::class.java)

        mediaSession.apply {
            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
            setCallback(mediaSessionCallback)
            setSessionActivity(PendingIntent.getActivity(applicationContext, 0, activityIntent, 0))
            setMediaButtonReceiver(PendingIntent.getBroadcast(applicationContext, 0, mediaButtonIntent, 0))
        }

        initializePlayer()
    }

    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {

            }

            AudioManager.AUDIOFOCUS_LOSS -> {

            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {

            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {

            }

            else -> mediaSessionCallback.onPause()
        }
    }

    private fun updateUI(track: Track) {
        EventBus.getDefault().post(track)
    }

    private val mediaSessionCallback: MediaSessionCompat.Callback = object : MediaSessionCompat.Callback() {

        //var currentState = PlaybackStateCompat.STATE_STOPPED

        override fun onPrepareFromMediaId(mediaId: String?, extras: Bundle) {
            super.onPrepareFromMediaId(mediaId, extras)

            val position = extras.getInt(Constants.POSITION)
            val track = mediaRepository.getTrackByPosition(position)

            if (track.id != mediaRepository.getCurrentTrack().id) {
                mediaRepository.setCurrentPosition(position)
                setAudioUri(track.uri)
                mediaSession.setMetadata(setMediaMetaData(track))
                updateUI(track)

                onPlay()

            } else if (mExoplayer.playWhenReady) {
                onPause()
            } else if (!mExoplayer.playWhenReady) {
                onPlay()
            }
        }

        override fun onPlay() {
            super.onPlay()

            if (!mExoplayer.playWhenReady) {
                startService(Intent(applicationContext, PlayerService::class.java))

                if (!isAudioFocusRequest) {


                    val audioFocusResult = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        audioManager.requestAudioFocus(audioFocusRequest)
                    } else {
                        audioManager.requestAudioFocus(
                            audioFocusChangeListener,
                            AudioManager.STREAM_MUSIC,
                            AudioManager.AUDIOFOCUS_GAIN
                        )
                    }

                    if (audioFocusResult != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                        return
                    }
                }
                isAudioFocusRequest = true
                mediaSession.isActive = true
                mExoplayer.playWhenReady = true

                mediaSession.setPlaybackState(
                    stateBuilder.setState(
                        PlaybackStateCompat.STATE_PLAYING,
                        PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1F
                    ).build()
                )
            }
        }

        override fun onPause() {
            super.onPause()

            if (mExoplayer.playWhenReady) {
                mExoplayer.playWhenReady = false

                mediaSession.setPlaybackState(
                    stateBuilder.setState(
                        PlaybackStateCompat.STATE_PAUSED,
                        PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1F
                    ).build()
                )
            }
        }

        override fun onSkipToNext() {
            super.onSkipToNext()
            mExoplayer.stop()
            val track = mediaRepository.getNextTrackByClick()
            setAudioUri(track.uri)
            updateUI(track)
            onPlay()

        }

        override fun onSkipToPrevious() {
            super.onSkipToPrevious()
            mExoplayer.stop()
            val track = mediaRepository.getPrevTrack()
            setAudioUri(track.uri)
            updateUI(track)
            onPlay()
        }

        override fun onSetShuffleMode(shuffleMode: Int) {
            super.onSetShuffleMode(shuffleMode)
            mediaRepository.setShuffleMode(shuffleMode)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        MediaButtonReceiver.handleIntent(mediaSession, intent)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return PlayerBinder()
    }

    inner class PlayerBinder: Binder() {
        fun getMediaSessionToken(): MediaSessionCompat.Token = mediaSession.sessionToken
    }

    private fun initializePlayer() {
        mExoplayer = ExoPlayerFactory.newSimpleInstance(
            DefaultRenderersFactory(this),
            DefaultTrackSelector(), DefaultLoadControl()
        )
    }

    private fun setAudioUri(uri: Uri) {
        val mediaSource = ExtractorMediaSource.Factory(DefaultDataSourceFactory(this, "vmplayer"))
            .createMediaSource(uri)
        mExoplayer.prepare(mediaSource)
    }

    private fun setMediaMetaData(track: Track): MediaMetadataCompat {
        return metadataBuilder
            //.putBitmap(MediaMetadataCompat.METADATA_KEY_ART, MediaStore.Images.Media.getBitmap(this.contentResolver, track.albumId))
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, track.title)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, track.artist)
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, track.albumName)
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, track.duration.toLong())
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaSession.release()
    }
}