package com.kuzheevadel.vmplayerv2.adapters

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.kuzheevadel.vmplayerv2.R
import com.kuzheevadel.vmplayerv2.common.Constants
import com.kuzheevadel.vmplayerv2.interfaces.MvpContracts
import com.kuzheevadel.vmplayerv2.model.Track
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.track_item_layout.view.*

class TracksRecyclerAdapter(var context: Context?): RecyclerView.Adapter<TracksRecyclerAdapter.TrackViewHolder>(), MvpContracts.TracksAdapter {

    private var tracksList= mutableListOf<Track>()
    private val mPicasso = Picasso.get()

    override fun updateTracksList(list: MutableList<Track>) {
        tracksList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(container: ViewGroup, position: Int): TrackViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.track_item_layout, container, false)
        return TrackViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tracksList.size
    }

    override fun onBindViewHolder(viewHolder: TrackViewHolder, position: Int) {
        viewHolder.bind(position)
    }

    inner class TrackViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val albumImage = view.item_image
        private val artistName = view.item_artist_name
        private val trackName = view.item_track_name

        init {
            view.setOnClickListener {
                Toast.makeText(context, "$layoutPosition touched", Toast.LENGTH_SHORT).show()
            }
        }

        fun bind(position: Int) {
            val track = tracksList[position]

            artistName.text = track.getNameAndDuration()
            trackName.text = track.title

            mPicasso.load(track.albumId)
                .centerCrop()
                .placeholder(R.drawable.vinil_default)
                .resize(100, 100)
                .transform(RoundedCornersTransformation(20, 3))
                .into(albumImage)

        }
    }
}