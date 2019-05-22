package com.kuzheevadel.vmplayerv2.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.databinding.library.baseAdapters.BR
import com.kuzheevadel.vmplayerv2.databinding.DetailAlbumItemBinding
import com.kuzheevadel.vmplayerv2.model.Track

class AlbumsTracksListAdapter: RecyclerView.Adapter<AlbumsTracksListAdapter.AlbumsTracksListViewHolder>() {

    var trackList = mutableListOf<Track>()

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
        viewHolder.binding?.textPosition?.text = (1 + pos).toString()
        viewHolder.binding?.setVariable(BR.albumsTrack, track)

        viewHolder.binding?.click = object : ClickHandler {
            override fun click(view: View) {
                Toast.makeText(view.context, "DetailAlbum", Toast.LENGTH_SHORT).show()
            }
        }

        viewHolder.binding?.executePendingBindings()
    }

    inner class AlbumsTracksListViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding: DetailAlbumItemBinding? = DataBindingUtil.bind(view)
    }
}