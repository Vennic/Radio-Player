package com.kuzheevadel.vmplayerv2.adapters

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.databinding.library.baseAdapters.BR
import com.kuzheevadel.vmplayerv2.common.Constants
import com.kuzheevadel.vmplayerv2.databinding.TrackItemLayoutBinding
import com.kuzheevadel.vmplayerv2.model.Track
import com.kuzheevadel.vmplayerv2.services.PlayerService

class TrackListAdapter(private val context: Context): RecyclerView.Adapter<TrackListAdapter.TrackListViewHolder>() {

    var trackList = mutableListOf<Track>()
    private var mediaControllerCompat: MediaControllerCompat? = null

    init {
        val callback = object : MediaControllerCompat.Callback() {
            override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                super.onPlaybackStateChanged(state)
            }
        }

        val serviConnection = object : ServiceConnection {

            override fun onServiceDisconnected(name: ComponentName?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
        context.bindService(Intent(context, PlayerService::class.java), serviConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): TrackListViewHolder {
        val inflater = LayoutInflater.from(p0.context)
        val binding = TrackItemLayoutBinding.inflate(inflater, p0, false)
        return TrackListViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    override fun onBindViewHolder(viewHolder: TrackListViewHolder, p1: Int) {
        val track = trackList[p1]
        val bundle = Bundle()
        viewHolder.binding?.setVariable(BR.track, track)
        bundle.putParcelable(Constants.PLAYING_TRACK, track)
        bundle.putInt(Constants.POSITION, p1)

        viewHolder.binding?.click = object : ClickHandler {

            override fun click(view: View) {
                mediaControllerCompat?.transportControls?.prepareFromMediaId("AllTracks", bundle)
            }

        }

        viewHolder.binding?.executePendingBindings()

    }

    inner class TrackListViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding: TrackItemLayoutBinding? = DataBindingUtil.bind(view)
    }

}