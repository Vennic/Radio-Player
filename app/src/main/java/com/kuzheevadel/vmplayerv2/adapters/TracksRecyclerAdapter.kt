package com.kuzheevadel.vmplayerv2.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.kuzheevadel.vmplayerv2.R
import kotlinx.android.synthetic.main.track_item_layout.view.*

class TracksRecyclerAdapter(var context: Context?): RecyclerView.Adapter<TracksRecyclerAdapter.TrackViewHolder>() {

    private val list= mutableListOf<Int>()

    init {
        for (i in 0..100) {
            list.add(i)
        }
    }

    override fun onCreateViewHolder(container: ViewGroup, position: Int): TrackViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.track_item_layout, container, false)
        return TrackViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(viewHolder: TrackViewHolder, position: Int) {
        viewHolder.bind(position)
    }

    inner class TrackViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val textView = view.textView

        init {
            view.setOnClickListener {
                Toast.makeText(context, "$layoutPosition touched", Toast.LENGTH_SHORT).show()
            }
        }

        fun bind(position: Int) {
            textView.text = list[position].toString()
        }
    }
}