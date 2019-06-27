package com.kuzheevadel.vmplayerv2.bindhelper

import android.arch.lifecycle.MutableLiveData
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import android.support.v4.media.session.MediaControllerCompat
import com.kuzheevadel.vmplayerv2.common.LoadStateMessage
import com.kuzheevadel.vmplayerv2.common.Source
import com.kuzheevadel.vmplayerv2.services.PlayerService
import org.greenrobot.eventbus.EventBus

class BindServiceHelper(private val context: Context) {

    private lateinit var serviceConnection: ServiceConnection
    var mediaControllerCompat: MediaControllerCompat? = null
    private var dataCallback: OnConnectionListener = object : OnConnectionListener {
        override fun setProgressData(data: MutableLiveData<Int>, source: Source) {

        }
    }


    interface OnConnectionListener {
        fun setProgressData(data: MutableLiveData<Int>, source: Source)
    }

    fun setOnConnectionListener(listener: OnConnectionListener) {
        dataCallback = listener
    }

    fun bindPlayerService(callback: MediaControllerCompat.Callback) {
        serviceConnection = object : ServiceConnection {

            override fun onServiceDisconnected(name: ComponentName?) {
                mediaControllerCompat = null
            }

            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val serviceBinder = service as PlayerService.PlayerBinder
                dataCallback.setProgressData(serviceBinder.getProgressData(), serviceBinder.getCurrentSource())

                try {
                    mediaControllerCompat = MediaControllerCompat(context, serviceBinder.getMediaSessionToken())
                    mediaControllerCompat?.registerCallback(callback)
                    callback.onPlaybackStateChanged(mediaControllerCompat?.playbackState)
                } catch (e: RemoteException) {
                    mediaControllerCompat = null
                }

                EventBus.getDefault().post(LoadStateMessage(isTracksLoaded = false, isConnected = true))
            }
        }

        context.bindService(Intent(context, PlayerService::class.java), serviceConnection, Context.BIND_AUTO_CREATE)

    }

    fun unbindPlayerService() {
        context.unbindService(serviceConnection)
    }
}