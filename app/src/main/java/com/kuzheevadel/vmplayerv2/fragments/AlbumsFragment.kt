package com.kuzheevadel.vmplayerv2.fragments

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kuzheevadel.vmplayerv2.R
import com.kuzheevadel.vmplayerv2.adapters.AlbumsListAdapter
import com.kuzheevadel.vmplayerv2.common.LoadMediaMessage
import com.kuzheevadel.vmplayerv2.dagger.App
import com.kuzheevadel.vmplayerv2.dagger.CustomViewModelFactory
import com.kuzheevadel.vmplayerv2.viewmodels.AlbumViewModel
import kotlinx.android.synthetic.main.albums_layout.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class AlbumsFragment: Fragment() {

    @Inject
    lateinit var mAdapter: AlbumsListAdapter

    @Inject
    lateinit var factory: CustomViewModelFactory

    lateinit var viewModel: AlbumViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)

        (activity?.application as App).getComponent().inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(AlbumViewModel::class.java)

        viewModel.setAdapter(mAdapter)

        mAdapter.setActivity(activity as AppCompatActivity)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.albums_layout, container, false)
        view.albums_recycler_view.layoutManager = GridLayoutManager(context, 2)
        view.albums_recycler_view.adapter = mAdapter
        return view
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun updateAdapter(event: LoadMediaMessage) {
        if (event.isLoaded) {
            viewModel.updateAdapter()
        }
    }
}