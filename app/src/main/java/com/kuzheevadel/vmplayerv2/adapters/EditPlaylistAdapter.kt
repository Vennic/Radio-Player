package com.kuzheevadel.vmplayerv2.adapters

import android.annotation.SuppressLint
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.android.databinding.library.baseAdapters.BR
import com.kuzheevadel.vmplayerv2.activities.EditPlaylistActivity
import com.kuzheevadel.vmplayerv2.databinding.EditPlaylistItemBinding
import com.kuzheevadel.vmplayerv2.model.Track
import java.util.*

class EditPlaylistAdapter: RecyclerView.Adapter<EditPlaylistAdapter.PlaylistListViewHolder>() {

    var trackList = mutableListOf<Track>()
    lateinit var activity: EditPlaylistActivity

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): PlaylistListViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val binding = EditPlaylistItemBinding.inflate(inflater, viewGroup, false)
        return PlaylistListViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(viewHolder: PlaylistListViewHolder, position: Int) {
        val track = trackList[viewHolder.layoutPosition]

        viewHolder.binding?.setVariable(BR.track, track)

        viewHolder.binding?.run {
            itemsMenu.setOnTouchListener { _, event ->

                if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                    activity.startDragging(viewHolder)
                }

                return@setOnTouchListener true
            }

            removeImage.setOnTouchListener { _, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                    activity.startSwiping(viewHolder)
                }

                return@setOnTouchListener true
            }
        }

        viewHolder.binding?.executePendingBindings()

    }

    fun moveItems(from: Int, to: Int) {
        Log.i("MOVETEST", "Before moving: $trackList")

        Collections.swap(trackList, from, to)
        Log.i("MOVETEST", "After moving: $trackList")

        this.notifyItemMoved(from, to)
    }

    fun removeItem(position: Int) {
        Log.i("MOVETEST", "Before removing: $trackList")

        trackList.removeAt(position)
        Log.i("MOVETEST", "After removing: $trackList")

        this.notifyItemRemoved(position)
    }

    inner class PlaylistListViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding: EditPlaylistItemBinding? = DataBindingUtil.bind(view)
    }
}