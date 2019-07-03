package com.kuzheevadel.vmplayerv2.adapters

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.databinding.library.baseAdapters.BR
import com.kuzheevadel.vmplayerv2.helper.BindServiceHelper
import com.kuzheevadel.vmplayerv2.common.Constants
import com.kuzheevadel.vmplayerv2.databinding.TrackItemLayoutBinding
import com.kuzheevadel.vmplayerv2.fragments.TrackBottomMenu
import com.kuzheevadel.vmplayerv2.interfaces.Interfaces
import com.kuzheevadel.vmplayerv2.model.Track

class PlaylistAdapter(private val mediaRepository: Interfaces.StorageMediaRepository,
                       private val bindServiceHelper: BindServiceHelper): RecyclerView.Adapter<PlaylistAdapter.PlaylistListViewHolder>() {

    var trackList = mutableListOf<Track>()
    var fm: FragmentManager? = null

    init {
        val callback = object : MediaControllerCompat.Callback() {
            override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                super.onPlaybackStateChanged(state)
            }
        }

        bindServiceHelper.bindPlayerService(callback)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): PlaylistListViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val binding = TrackItemLayoutBinding.inflate(inflater, viewGroup, false)
        return PlaylistListViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    override fun onBindViewHolder(viewHolder: PlaylistListViewHolder, position: Int) {
        val track = trackList[position]
        val bundle = Bundle()
        viewHolder.binding?.setVariable(BR.track, track)
        bundle.putInt(Constants.POSITION, position)

        viewHolder.binding?.click = object : ClickHandler {

            override fun click(view: View) {
                mediaRepository.isPlaylist = true
                mediaRepository.setPlayingTrackList(trackList)
                bindServiceHelper.mediaControllerCompat?.transportControls?.prepareFromMediaId(Constants.TRACK, bundle)
            }

        }

        viewHolder.binding?.itemsMenu?.setOnClickListener {
            val bottomDialog = TrackBottomMenu()
            bottomDialog.show(fm, bottomDialog.tag)
        }

        viewHolder.binding?.executePendingBindings()

    }

    inner class PlaylistListViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding: TrackItemLayoutBinding? = DataBindingUtil.bind(view)
    }

    fun unbindService() {
        bindServiceHelper.unbindPlayerService()
    }

}