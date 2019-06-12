package com.kuzheevadel.vmplayerv2.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.kuzheevadel.vmplayerv2.R
import com.kuzheevadel.vmplayerv2.adapters.TrackListAdapter
import com.kuzheevadel.vmplayerv2.dagger.App
import com.kuzheevadel.vmplayerv2.dagger.CustomViewModelFactory
import com.kuzheevadel.vmplayerv2.database.PlaylistDatabase
import com.kuzheevadel.vmplayerv2.model.Track
import com.kuzheevadel.vmplayerv2.services.State
import com.kuzheevadel.vmplayerv2.viewmodels.PlaylistViewModel
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.playlist_layout.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.Callable
import javax.inject.Inject

class PlaylistFragment: Fragment() {

    @Inject
    lateinit var database: PlaylistDatabase

    @Inject
    lateinit var factory: CustomViewModelFactory

    @Inject
    lateinit var mAdapter: TrackListAdapter

    lateinit var viewModel: PlaylistViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as App).getComponent().inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(PlaylistViewModel::class.java)
        mAdapter.fm = activity?.supportFragmentManager

        viewModel.apply {
            trackData.observe(this@PlaylistFragment, Observer { mAdapter.trackList = it!! })
            loadStatus.observe(this@PlaylistFragment, Observer {
                if (it == State.ERROR) {
                    Toast.makeText(context, "Cannot load playlist", Toast.LENGTH_LONG).show()
                } else {
                    mAdapter.notifyDataSetChanged()
                }
            })
        }
        viewModel.loadPlaylistFromDatabase()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.playlist_layout, container, false)

        view.playlist_recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun refreshAdpter(post: String) {
        viewModel.loadPlaylistFromDatabase()
    }

}