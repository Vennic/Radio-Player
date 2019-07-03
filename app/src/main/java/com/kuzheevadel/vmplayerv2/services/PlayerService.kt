package com.kuzheevadel.vmplayerv2.services

import android.app.*
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.ContextCompat
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
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.kuzheevadel.vmplayerv2.R
import com.kuzheevadel.vmplayerv2.common.Constants
import com.kuzheevadel.vmplayerv2.common.Source
import com.kuzheevadel.vmplayerv2.common.UpdateUIMessage
import com.kuzheevadel.vmplayerv2.dagger.App
import com.kuzheevadel.vmplayerv2.helper.PlayerStyleHelper
import com.kuzheevadel.vmplayerv2.interfaces.Interfaces
import com.kuzheevadel.vmplayerv2.model.RadioStation
import com.kuzheevadel.vmplayerv2.model.Track
import com.kuzheevadel.vmplayerv2.repository.RadioRepository
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
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

    @Inject
    lateinit var radioRepository: RadioRepository

    private lateinit var disposable: CompositeDisposable
    private lateinit var subscription: Disposable
    val progressData: MutableLiveData<Int> = MutableLiveData()
    private var source = Source.TRACK
    private var currentState = PlaybackStateCompat.STATE_STOPPED

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

    private val target = object : Target {
        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
        }

        override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
        }

        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
            mediaSession.setMetadata(setRadioMediaMetaData(radioRepository.currentPlayingStation!!, bitmap))
            refreshNotification(currentState)
        }
    }

    override fun onCreate() {
        super.onCreate()
        (application as App).getComponent().inject(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notificationChannel = NotificationChannel(Constants.NOTIFICATION_DEFAULT_CHANNEL, "vmplayer", NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)


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

        //val activityIntent = Intent(applicationContext, PlayerActivity::class.java)
        val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON, null, applicationContext, MediaButtonReceiver::class.java)

        mediaSession.apply {
            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
            setCallback(mediaSessionCallback)
            //setSessionActivity(PendingIntent.getActivity(applicationContext, 0, activityIntent, 0))
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
                    mediaSession.setMetadata(setTrackMediaMetaData(track))
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
            } else if (mediaId == Constants.INIT) {
                Log.i("PrefTest", "prepare")
                val id = extras.getLong(Constants.ID)
                val track = mediaRepository.getTrackById(id)

                setAudioUri(track.getAudioUri())
                mediaSession.setMetadata(setTrackMediaMetaData(track))
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
            } else {
                val uri = Uri.parse(extras.getString(Constants.RADIO_URL))
                val name = extras.getString(Constants.RADIO_TITLE)
                val imageUrl = extras.getString(Constants.RADIO_IMAGE)
                val radioId = extras.getString(Constants.RADIO_ID).toLong()
                //mediaSession.setMetadata(setRadioMediaMetaData(radioRepository.currentPlayingStation!!))
                Picasso.get().load(radioRepository.currentPlayingStation!!.favicon).into(target)
                currentPlayingTrackId = -1
                source = Source.RADIO
                updateTrackUI(UpdateUIMessage("", name, 0, Uri.parse(imageUrl), 0, "", Source.RADIO, radioId, false))
                setAudioUri(uri)

                onPlay()

            }
        }

        override fun onPlay() {
            super.onPlay()

            if (!mExoplayer.playWhenReady) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    startForegroundService(Intent(applicationContext, PlayerService::class.java))
                } else {
                    startService(Intent(applicationContext, PlayerService::class.java))
                }

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
            currentState = PlaybackStateCompat.STATE_PLAYING
            refreshNotification(currentState)
        }

        override fun onPause() {
            super.onPause()
            stopSelf()
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
            currentState = PlaybackStateCompat.STATE_PAUSED
            refreshNotification(currentState)
        }

        override fun onSeekTo(pos: Long) {
            super.onSeekTo(pos)
            mExoplayer.seekTo(pos)
        }

        override fun onSkipToNext() {
            super.onSkipToNext()
            mExoplayer.stop()
            val track = mediaRepository.getNextTrackByClick()
            mediaSession.setMetadata(setTrackMediaMetaData(track))
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
            mediaSession.setMetadata(setTrackMediaMetaData(track))
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

    private fun setTrackMediaMetaData(track: Track): MediaMetadataCompat {
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, track.getImageUri())

        return metadataBuilder
            .putBitmap(MediaMetadataCompat.METADATA_KEY_ART, bitmap)
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, track.title)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, track.artist)
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, track.albumName)
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, track.duration.toLong())
            .build()
    }

    private fun setRadioMediaMetaData(radioStation: RadioStation, bitmap: Bitmap?): MediaMetadataCompat {
        mediaSession.setMetadata(null)

        return metadataBuilder
            .putBitmap(MediaMetadataCompat.METADATA_KEY_ART, bitmap)
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, radioStation.name)
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, radioStation.country)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, radioStation.getTagsInfo())
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0)
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

    private fun refreshNotification(playbackState: Int) {
        when (playbackState) {
            PlaybackStateCompat.STATE_PLAYING -> startForeground(Constants.NOTIFICATION_ID, getNotification(playbackState))
            PlaybackStateCompat.STATE_PAUSED -> {
                NotificationManagerCompat.from(this).notify(Constants.NOTIFICATION_ID, getNotification(playbackState))
                stopForeground(false)
            }
            else -> stopForeground(true)
        }
    }

    private fun getNotification(playbackState: Int): Notification {
        val builder: NotificationCompat.Builder = PlayerStyleHelper.from(applicationContext, mediaSession)
        builder.addAction(
            NotificationCompat.Action(
                R.drawable.ic_skip_previous_black_24dp, "Previous",
                MediaButtonReceiver.buildMediaButtonPendingIntent(applicationContext, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
            )
        )

        if (playbackState == PlaybackStateCompat.STATE_PLAYING) {
            builder.addAction(
                NotificationCompat.Action(
                    R.drawable.ic_pause_black_24dp, "Pause",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(applicationContext, PlaybackStateCompat.ACTION_PLAY_PAUSE)
                )
            )
        } else {
            builder.addAction(
                NotificationCompat.Action(
                    R.drawable.ic_play_arrow_black_24dp, "Play",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(applicationContext, PlaybackStateCompat.ACTION_PLAY_PAUSE)
                )
            )
        }

        builder.addAction(
            NotificationCompat.Action(
                R.drawable.ic_skip_next_black_24dp, "Next",
                MediaButtonReceiver.buildMediaButtonPendingIntent(applicationContext, PlaybackStateCompat.ACTION_SKIP_TO_NEXT)
            )
        )

        builder.setStyle(
            android.support.v4.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0, 1, 2)
                .setShowCancelButton(true)
                .setCancelButtonIntent(
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        applicationContext,
                        PlaybackStateCompat.ACTION_STOP
                    )
                )
        )

        builder.setSmallIcon(R.mipmap.ic_launcher)
        builder.color = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        builder.setShowWhen(false)
        builder.priority = NotificationCompat.PRIORITY_HIGH
        builder.setOnlyAlertOnce(true)
        builder.setChannelId(Constants.NOTIFICATION_DEFAULT_CHANNEL)
        return builder.build()
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