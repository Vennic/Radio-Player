package com.kuzheevadel.vmplayerv2.activities

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.transition.Fade
import android.view.View
import com.kuzheevadel.vmplayerv2.R
import com.kuzheevadel.vmplayerv2.adapters.AlbumsTrackList
import com.kuzheevadel.vmplayerv2.di.App
import com.kuzheevadel.vmplayerv2.di.CustomViewModelFactory
import com.kuzheevadel.vmplayerv2.fragments.FullScreenPlaybackFragment
import com.kuzheevadel.vmplayerv2.viewmodels.DetailAlbumViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.album_activity_layout.*
import javax.inject.Inject

class AlbumActivity: AppCompatActivity() {

    @Inject
    lateinit var factory: CustomViewModelFactory

    lateinit var viewModel: DetailAlbumViewModel

    @Inject
    lateinit var adapter: AlbumsTrackList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val picasso = Picasso.get()
        setContentView(R.layout.album_activity_layout)
        setSupportActionBar(player_album_toolbar)

        (application as App).getComponent().inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(DetailAlbumViewModel::class.java)
        viewModel.setAdapter(adapter)

        val position = intent.getIntExtra("Position", 0)
        val uri = intent.getStringExtra("Album's Uri")
        detail_album_text.text = intent.getStringExtra("Text")

        albums_recycler_detail.layoutManager = LinearLayoutManager(this)
        albums_recycler_detail.adapter = adapter

        viewModel.setAlbum(position)

        picasso.load(uri)
            .fit()
            .into(albums_detail_image)

        supportFragmentManager.beginTransaction()
            .replace(R.id.playback_container_detail, FullScreenPlaybackFragment(), null)
            .commit()
    }
}