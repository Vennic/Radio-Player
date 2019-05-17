package com.kuzheevadel.vmplayerv2.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kuzheevadel.vmplayerv2.R
import com.kuzheevadel.vmplayerv2.model.Track
import kotlinx.android.synthetic.main.detail_album_item.view.*

class AlbumsTrackList(val context: Context): RecyclerView.Adapter<AlbumsTrackList.AlbumsTrackListViewHolder>() {

    private lateinit var trackList: MutableList<Track>

    fun setList(list: MutableList<Track>) {
        trackList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(container: ViewGroup, position: Int): AlbumsTrackListViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.detail_album_item, container, false)
        return AlbumsTrackListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    override fun onBindViewHolder(viewHolder: AlbumsTrackListViewHolder, position: Int) {
        viewHolder.bind(position)
    }


    inner class AlbumsTrackListViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val textPosition = view.text_position
        private val artistName = view.album_artist_name
        private val trackTitle = view.album_track_title

        fun bind(pos: Int) {
            val track = trackList[pos]

            textPosition.text = (pos + 1).toString()
            artistName.text = track.getNameAndDuration()
            trackTitle.text = track.title
        }
    }
}