package com.kuzheevadel.vmplayerv2.adapters

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.provider.SyncStateContract
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.databinding.library.baseAdapters.BR
import com.kuzheevadel.vmplayerv2.Helpers.BindServiceHelper
import com.kuzheevadel.vmplayerv2.common.Constants
import com.kuzheevadel.vmplayerv2.databinding.DetailAlbumItemBinding
import com.kuzheevadel.vmplayerv2.interfaces.Interfaces
import com.kuzheevadel.vmplayerv2.model.Track

class AlbumsTracksListAdapter(private val mediaRepository: Interfaces.StorageMediaRepository,
                              private val bindServiceHelper: BindServiceHelper): RecyclerView.Adapter<AlbumsTracksListAdapter.AlbumsTracksListViewHolder>() {

    var trackList = mutableListOf<Track>()

    init {
        val callback = object : MediaControllerCompat.Callback() {
            override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                super.onPlaybackStateChanged(state)
            }
        }

        bindServiceHelper.bindPlayerService(callback)
    }

    override fun onCreateViewHolder(parent: ViewGroup, pos: Int): AlbumsTracksListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DetailAlbumItemBinding.inflate(inflater, parent, false)
        return AlbumsTracksListViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    override fun onBindViewHolder(viewHolder: AlbumsTracksListViewHolder, pos: Int) {
        val track = trackList[pos]
        val bundle = Bundle()
        bundle.putInt(Constants.POSITION, pos)
        viewHolder.binding?.textPosition?.text = (1 + pos).toString()
        viewHolder.binding?.setVariable(BR.albumsTrack, track)

        viewHolder.binding?.click = object : ClickHandler {
            override fun click(view: View) {
                mediaRepository.setPlayingTrackList(trackList)
                bindServiceHelper.mediaControllerCompat?.transportControls?.prepareFromMediaId("Detail", bundle)
            }
        }

        viewHolder.binding?.executePendingBindings()
    }

    fun unbindService() {
        bindServiceHelper.unbindPlayerService()
    }

    inner class AlbumsTracksListViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding: DetailAlbumItemBinding? = DataBindingUtil.bind(view)
    }
}