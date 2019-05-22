package com.kuzheevadel.vmplayerv2.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.databinding.library.baseAdapters.BR
import com.kuzheevadel.vmplayerv2.databinding.TrackItemLayoutBinding
import com.kuzheevadel.vmplayerv2.model.Track

class TrackListAdapter: RecyclerView.Adapter<TrackListAdapter.TrackListViewHolder>() {

    var trackList = mutableListOf<Track>()

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
        viewHolder.binding?.setVariable(BR.track, track)

        viewHolder.binding?.click = object : ClickHandler {

            override fun click(view: View) {
                Toast.makeText(viewHolder.itemView.context, "Touched ${viewHolder.adapterPosition}", Toast.LENGTH_SHORT).show()
            }

        }

        viewHolder.binding?.executePendingBindings()

    }

    inner class TrackListViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding: TrackItemLayoutBinding? = DataBindingUtil.bind(view)
    }

}