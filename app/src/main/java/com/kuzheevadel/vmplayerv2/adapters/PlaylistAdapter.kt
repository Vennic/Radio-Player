package com.kuzheevadel.vmplayerv2.adapters

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v4.media.session.MediaControllerCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.databinding.library.baseAdapters.BR
import com.kuzheevadel.vmplayerv2.common.Constants
import com.kuzheevadel.vmplayerv2.databinding.PlaylistItemLayoutBinding
import com.kuzheevadel.vmplayerv2.fragments.PlaylistFragment
import com.kuzheevadel.vmplayerv2.helper.BindServiceHelper
import com.kuzheevadel.vmplayerv2.interfaces.Interfaces
import com.kuzheevadel.vmplayerv2.model.Track

class PlaylistAdapter(private val mediaRepository: Interfaces.StorageMediaRepository,
                      private val bindServiceHelper: BindServiceHelper): RecyclerView.Adapter<PlaylistAdapter.PlaylistListViewHolder>() {

    var trackList = mutableListOf<Track>()
    var fm: FragmentManager? = null
    lateinit var fragment: PlaylistFragment

    init {
        val callback = object : MediaControllerCompat.Callback() {
        }

        bindServiceHelper.bindPlayerService(callback)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): PlaylistListViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val binding = PlaylistItemLayoutBinding.inflate(inflater, viewGroup, false)
        return PlaylistListViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    override fun onBindViewHolder(viewHolder: PlaylistListViewHolder, position: Int) {
        val track = trackList[viewHolder.layoutPosition]

        val bundle = Bundle()
        viewHolder.binding?.setVariable(BR.track, track)

        viewHolder.binding?.click = object : ClickHandler {

            override fun click(view: View) {
                bundle.putInt(Constants.POSITION, viewHolder.layoutPosition)

                mediaRepository.isPlaylist = true
                mediaRepository.setPlayingTrackList(trackList)
                bindServiceHelper.mediaControllerCompat?.transportControls?.prepareFromMediaId(Constants.TRACK, bundle)
            }

        }

        viewHolder.binding?.executePendingBindings()

    }

    inner class PlaylistListViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding: PlaylistItemLayoutBinding? = DataBindingUtil.bind(view)
    }

    fun unbindService() {
        bindServiceHelper.unbindPlayerService()
    }

}