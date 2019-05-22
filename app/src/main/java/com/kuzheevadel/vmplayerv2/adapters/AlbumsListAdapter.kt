package com.kuzheevadel.vmplayerv2.adapters

import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.databinding.library.baseAdapters.BR
import com.kuzheevadel.vmplayerv2.activities.AlbumActivity
import com.kuzheevadel.vmplayerv2.common.Constants
import com.kuzheevadel.vmplayerv2.databinding.AlbumItemBinding
import com.kuzheevadel.vmplayerv2.model.Album

class AlbumsListAdapter: RecyclerView.Adapter<AlbumsListAdapter.AlbumsListViewHolder>() {

    private lateinit var activity: AppCompatActivity
    var albumsList = mutableListOf<Album>()

    override fun onCreateViewHolder(parent: ViewGroup, pos: Int): AlbumsListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AlbumItemBinding.inflate(inflater, parent, false)
        return AlbumsListViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return albumsList.size
    }

    override fun onBindViewHolder(viewHolder: AlbumsListViewHolder, pos: Int) {
        val album = albumsList[pos]
        viewHolder.binding?.setVariable(BR.album, album)

        viewHolder.binding?.click = object : ClickHandler {
            override fun click(view: View) {

                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                    viewHolder.binding?.cardAlbumsImage as View,
                    ViewCompat.getTransitionName(viewHolder.binding.cardAlbumsImage)!!)

                val intent = Intent(activity, AlbumActivity::class.java)
                intent.putExtra(Constants.ALBUMS_URI, album.getAlbumImageUri().toString())
                intent.putExtra(Constants.ALBUMS_TITLE, album.title)
                intent.putExtra(Constants.POSITION, viewHolder.adapterPosition)
                activity.startActivity(intent, options.toBundle())
            }
        }
    }

    fun setActivity(activity: AppCompatActivity) {
        this.activity = activity
    }

    inner class AlbumsListViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding: AlbumItemBinding? = DataBindingUtil.bind(view)
    }

}
