package com.kuzheevadel.vmplayerv2.services

import android.app.PendingIntent
import android.app.Service
import android.arch.lifecycle.MutableLiveData
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
import android.util.Log
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.kuzheevadel.vmplayerv2.activities.PlayerActivity
import com.kuzheevadel.vmplayerv2.common.Constants
import com.kuzheevadel.vmplayerv2.common.Source
import com.kuzheevadel.vmplayerv2.common.UpdateUIMessage
import com.kuzheevadel.vmplayerv2.dagger.App
import com.kuzheevadel.vmplayerv2.interfaces.Interfaces
import com.kuzheevadel.vmplayerv2.model.Track
import com.kuzheevadel.vmplayerv2.repository.RadioRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PlayerService: Service() {

    @Inject
    lateinit var mediaRepository: Interfaces.StorageMediaRepository

    private lateinit var disposable: CompositeDisposable
    private lateinit var subscription: Disposable
    val progressData: MutableLiveData<Int> = MutableLiveData()
    private var source = Source.TRACK

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

    private fun updateTrackUI(message: UpdateUIMessage) {
        EventBus.getDefault().post(message)
    }

    private val mediaSessionCallback: MediaSessionCompat.Callback = object : MediaSessionCompat.Callback() {

        //var currentState = PlaybackStateCompat.STATE_STOPPED
        private var currentPlayingTrackId: Long = -1

        override fun onPrepareFromMediaId(mediaId: String?, extras: Bundle) {
            super.onPrepareFromMediaId(mediaId, extras)

            if (mediaId == Constants.TRACK) {
                val position = extras.getInt(Constants.POSITION)

                val track = mediaRepository.getTrackByPosition(position)

                Log.i("PLAYERTEST", track.inPlaylist.toString())

                if (track.id != currentPlayingTrackId) {
                    mediaRepository.setCurrentPosition(position)
                    setAudioUri(track.getAudioUri())
                    mediaSession.setMetadata(setMediaMetaData(track))
                    currentPlayingTrackId = track.id
                    source = Source.TRACK

                    with(track) {
                        updateTrackUI(UpdateUIMessage(title,
                            artist,
                            albumId,
                            null,
                            duration,
                            albumName,
                            Source.TRACK,
                            id,
                            track.inPlaylist))
                    }


                    onPlay()

                } else if (mExoplayer.playWhenReady) {
                    onPause()
                } else if (!mExoplayer.playWhenReady) {
                    onPlay()
                }
            } else {
                val uri = Uri.parse(extras.getString(Constants.RADIO_URL))
                val name = extras.getString(Constants.RADIO_TITLE)
                val imageUrl = extras.getString(Constants.RADIO_IMAGE)
                source = Source.RADIO
                updateTrackUI(UpdateUIMessage("", name, 0, Uri.parse(imageUrl), 0, "", Source.RADIO, -1, false))
                setAudioUri(uri)
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
                startInterval()

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
                stopInterval()

                mediaSession.setPlaybackState(
                    stateBuilder.setState(
                        PlaybackStateCompat.STATE_PAUSED,
                        PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1F
                    ).build()
                )
            }
        }

        override fun onSeekTo(pos: Long) {
            super.onSeekTo(pos)
            mExoplayer.seekTo(pos)
        }

        override fun onSkipToNext() {
            super.onSkipToNext()
            mExoplayer.stop()
            val track = mediaRepository.getNextTrackByClick()
            currentPlayingTrackId = track.id
            setAudioUri(track.getAudioUri())
            source = Source.TRACK

            with(track) {
                updateTrackUI(UpdateUIMessage(title, artist, albumId, null, duration, albumName, Source.TRACK, id, track.inPlaylist))
            }

            onPlay()

        }

        override fun onSkipToPrevious() {
            super.onSkipToPrevious()
            mExoplayer.stop()
            val track = mediaRepository.getPrevTrack()
            currentPlayingTrackId = track.id
            setAudioUri(track.getAudioUri())
            source = Source.TRACK

            with(track) {
                updateTrackUI(UpdateUIMessage(title, artist, albumId, null, duration, albumName, Source.TRACK, id, track.inPlaylist))
            }

            onPlay()
        }

        override fun onSetShuffleMode(shuffleMode: Int) {
            super.onSetShuffleMode(shuffleMode)
            if (shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_NONE) {
                mediaRepository.setShuffleMode(Constants.SHUFFLE_MODE_OFF)
            } else {
                mediaRepository.setShuffleMode(Constants.SHUFFLE_MODE_ON)
            }
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
        fun getProgressData(): MutableLiveData<Int> {
            return progressData
        }
        fun getCurrentSource() = source
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

    private fun setHlsMediaSource(uri: Uri) {
        val mediaSource = HlsMediaSource.Factory(DefaultDataSourceFactory(this, Util.getUserAgent(this, "VMPlayer")))
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

    private fun stopInterval() {
        disposable.dispose()
    }

    private fun startInterval() {
        disposable = CompositeDisposable()

        subscription = Observable.interval(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (mExoplayer.playWhenReady) {
                    progressData.value = (mExoplayer.currentPosition / 1000).toInt()
                } else {
                    disposable.dispose()
                }
            },
                {
                    Log.i("Error Log", "${it.stackTrace}")
                })

        disposable.add(subscription)
    }

    override fun onDestroy() {
        try {
            disposable.dispose()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mediaSession.release()

        super.onDestroy()
    }
}