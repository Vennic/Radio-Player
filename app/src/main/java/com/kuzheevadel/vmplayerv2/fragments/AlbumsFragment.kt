package com.kuzheevadel.vmplayerv2.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kuzheevadel.vmplayerv2.R
import com.kuzheevadel.vmplayerv2.adapters.AlbumsAdapter
import com.kuzheevadel.vmplayerv2.common.LoadMediaMessage
import com.kuzheevadel.vmplayerv2.dagger.App
import com.kuzheevadel.vmplayerv2.interfaces.MvpContracts
import kotlinx.android.synthetic.main.albums_layout.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class AlbumsFragment: Fragment() {

    @Inject
    lateinit var mAdapter: AlbumsAdapter

    @Inject
    lateinit var mPresenter: MvpContracts.AlbumsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.instance.createAlbumsComponent()?.inject(this)
        mPresenter.setAdapter(mAdapter)
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

    override fun onDestroy() {
        App.instance.releaseAlbumsComponent()
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateAdapter(event: LoadMediaMessage) {
        if (event.isLoaded) {
            mPresenter.updateAdapter()
        }
    }
}