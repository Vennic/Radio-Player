package com.kuzheevadel.vmplayerv2.activities

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.kuzheevadel.vmplayerv2.R
import com.kuzheevadel.vmplayerv2.adapters.AlbumsTracksListAdapter
import com.kuzheevadel.vmplayerv2.common.Constants
import com.kuzheevadel.vmplayerv2.dagger.App
import com.kuzheevadel.vmplayerv2.dagger.CustomViewModelFactory
import com.kuzheevadel.vmplayerv2.fragments.FullScreenPlaybackFragment
import com.kuzheevadel.vmplayerv2.viewmodels.DetailAlbumViewModel
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.album_activity_layout.*
import javax.inject.Inject

class AlbumActivity: AppCompatActivity() {

    @Inject
    lateinit var factory: CustomViewModelFactory

    private lateinit var viewModel: DetailAlbumViewModel

    @Inject
    lateinit var adapter: AlbumsTracksListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(Constants.themeId)
        super.onCreate(savedInstanceState)
        val picasso = Picasso.get()
        setContentView(R.layout.album_activity_layout)
        setSupportActionBar(player_album_toolbar)

        (application as App).getComponent().inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(DetailAlbumViewModel::class.java)
        viewModel.setAdapter(adapter)

        val position = intent.getIntExtra(Constants.POSITION, 0)
        val uri = intent.getStringExtra(Constants.ALBUMS_URI)
        detail_album_text.text = intent.getStringExtra(Constants.ALBUMS_TITLE)

        albums_recycler_detail.layoutManager = LinearLayoutManager(this)
        albums_recycler_detail.adapter = adapter

        viewModel.setAlbum(position)

        picasso.load(uri)
            .fit()
            .placeholder(R.drawable.vinil_default)
            .into(albums_detail_image)

        supportFragmentManager.beginTransaction()
            .replace(R.id.playback_container_detail, FullScreenPlaybackFragment(), null)
            .commit()
    }

    override fun onDestroy() {
        adapter.unbindService()
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (activity_album_root != null &&
            (activity_album_root.panelState == SlidingUpPanelLayout.PanelState.EXPANDED || activity_album_root.panelState == SlidingUpPanelLayout.PanelState.ANCHORED)){
            activity_album_root.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
        } else {
            super.onBackPressed()
        }
    }
}