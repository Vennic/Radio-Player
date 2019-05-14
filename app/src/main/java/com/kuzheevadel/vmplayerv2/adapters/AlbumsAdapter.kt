package com.kuzheevadel.vmplayerv2.adapters

import android.content.Context
import android.content.Intent
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kuzheevadel.vmplayerv2.R
import com.kuzheevadel.vmplayerv2.activities.AlbumActivity
import com.kuzheevadel.vmplayerv2.interfaces.Interfaces
import com.kuzheevadel.vmplayerv2.model.Album
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.album_item.view.*


class AlbumsAdapter(private val context: Context): RecyclerView.Adapter<AlbumsAdapter.AlbumsViewHolder>(), Interfaces.AlbumsAdapter {

    private var albumsList = mutableListOf<Album>()
    private val mPicasso = Picasso.get()
    private lateinit var fragmentManager: FragmentManager
    private lateinit var activity: AppCompatActivity

    override fun updateAlbumsAdapter(list: MutableList<Album>) {
        albumsList = list
        notifyDataSetChanged()
    }

    fun setActivity(act: AppCompatActivity) {
        activity = act
    }

    override fun setFragmentManager(fm: FragmentManager) {
        fragmentManager = fm
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
        init {
            view.setOnClickListener {
                /*fragmentManager.beginTransaction()
                    .replace(R.id.root_frag, DetailAlbumFragment())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(null)
                    .commit()*/

                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity)
                val intent = Intent(context, AlbumActivity::class.java)
                context.startActivity(intent)
            }
        }
        fun bind(position: Int) {
            val album = albumsList[position]
            albumText.text = album.title
            albumText.isSelected = true
            val uri = album.tracksList[0].albumId

            mPicasso.load(uri)
                .fit()
                .placeholder(R.drawable.vinil_default)
                .into(albumImage)
        }
    }
}