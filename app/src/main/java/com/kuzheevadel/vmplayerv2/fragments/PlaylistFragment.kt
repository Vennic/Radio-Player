package com.kuzheevadel.vmplayerv2.fragments

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kuzheevadel.vmplayerv2.R
import com.kuzheevadel.vmplayerv2.adapters.PlaylistAdapter
import com.kuzheevadel.vmplayerv2.common.RewriteDoneMessage
import com.kuzheevadel.vmplayerv2.common.State
import com.kuzheevadel.vmplayerv2.dagger.App
import com.kuzheevadel.vmplayerv2.dagger.CustomViewModelFactory
import com.kuzheevadel.vmplayerv2.database.PlaylistDatabase
import com.kuzheevadel.vmplayerv2.model.Track
import com.kuzheevadel.vmplayerv2.viewmodels.PlaylistViewModel
import kotlinx.android.synthetic.main.playlist_layout.view.*
import kotlinx.android.synthetic.main.view_state_layout.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class PlaylistFragment: Fragment() {

    @Inject
    lateinit var database: PlaylistDatabase

    @Inject
    lateinit var factory: CustomViewModelFactory

    @Inject
    lateinit var mAdapter: PlaylistAdapter

    private lateinit var loadStateData: MutableLiveData<State>
    private var list = mutableListOf<Track>()

    companion object {
        fun getInstance(loadStateData: MutableLiveData<State>): PlaylistFragment {
            val instance = PlaylistFragment()
            instance.loadStateData = loadStateData
            return instance
        }
    }

    private lateinit var viewModel: PlaylistViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
        EventBus.getDefault().register(this)
        (activity?.application as App).getComponent().inject(this)
        mAdapter.fragment = this

        viewModel = ViewModelProviders.of(this, factory).get(PlaylistViewModel::class.java)
        mAdapter.fm = activity?.supportFragmentManager

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.playlist_layout, container, false)

        view.playlist_recycler.run {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }

        view.playlist_view_state_layout.reload_button.setOnClickListener { viewModel.loadPlaylistFromDatabase() }

        loadStateData.observe(this, Observer {
            when (it) {
                State.LOADING -> {}
                State.DONE -> {
                    viewModel.loadPlaylistFromDatabase()
                }
                State.ERROR -> view.playlist_view_state_layout.visibility = View.VISIBLE
            }
        })

        viewModel.run {

            trackData.observe(this@PlaylistFragment, Observer {
                list = it!!
                mAdapter.trackList = list })

            loadStatus.observe(this@PlaylistFragment, Observer {
                if (it == State.ERROR) {
                    view.playlist_view_state_layout.visibility = View.VISIBLE
                    view.playlist_recycler.visibility = View.GONE
                } else {
                    view.playlist_view_state_layout.visibility = View.GONE
                    view.playlist_recycler.visibility = View.VISIBLE
                    mAdapter.notifyDataSetChanged()
                }
            })
        }

        return view
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        mAdapter.unbindService()
        super.onDestroy()
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun refreshAdapter(post: String) {
        if (post == "track") {
            viewModel.loadPlaylistFromDatabase()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun reloadPlaylist(message: RewriteDoneMessage) {
        if (message.isRewrited) {
            viewModel.loadPlaylistFromDatabase()
        }
    }
}