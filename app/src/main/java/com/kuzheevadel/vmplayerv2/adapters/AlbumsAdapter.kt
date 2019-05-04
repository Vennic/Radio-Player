package com.kuzheevadel.vmplayerv2.adapters

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kuzheevadel.vmplayerv2.R
import com.kuzheevadel.vmplayerv2.interfaces.MvpContracts
import com.kuzheevadel.vmplayerv2.model.Album
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.album_item.view.*

class AlbumsAdapter(private val context: Context): RecyclerView.Adapter<AlbumsAdapter.AlbumsViewHolder>(), MvpContracts.AlbumsAdapter {

    private var albumsList = mutableListOf<Album>()
    private val mPicasso = Picasso.get()

    override fun updateAlbumsAdapter(list: MutableList<Album>) {
        albumsList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(container: ViewGroup, position: Int): AlbumsViewHolder {
        return AlbumsViewHolder(LayoutInflater.from(context).inflate(R.layout.album_item, container, false))
    }

    override fun getItemCount(): Int {
        return albumsList.size
    }

    override fun onBindViewHolder(viewHolder: AlbumsViewHolder, position: Int) {
        viewHolder.bind(position)
    }

    inner class AlbumsViewHolder(view: View): RecyclerView.ViewHolder(view) {
        private val albumImage: AppCompatImageView = view.card_albums_image
        private val albumText = view.card_albums_text

        fun bind(position: Int) {
            val album = albumsList[position]
            albumText.text = album.title
            val uri = album.tracksList[0].albumId

            mPicasso.load(uri)

                .into(albumImage)
        }
    }
}