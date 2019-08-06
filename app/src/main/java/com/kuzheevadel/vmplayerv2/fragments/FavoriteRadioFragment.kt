package com.kuzheevadel.vmplayerv2.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.kuzheevadel.vmplayerv2.R
import com.kuzheevadel.vmplayerv2.adapters.RadioStationsAdapter
import com.kuzheevadel.vmplayerv2.common.State
import com.kuzheevadel.vmplayerv2.dagger.App
import com.kuzheevadel.vmplayerv2.dagger.CustomViewModelFactory
import com.kuzheevadel.vmplayerv2.viewmodels.FavoriteRadioViewModel
import kotlinx.android.synthetic.main.favorite_radio_layout.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class FavoriteRadioFragment: Fragment() {

    @Inject
    lateinit var mAdapter: RadioStationsAdapter

    @Inject
    lateinit var mFactory: CustomViewModelFactory

    private lateinit var viewModel: FavoriteRadioViewModel

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (activity?.application as App).getComponent().inject(this)
        viewModel = ViewModelProviders.of(this, mFactory).get(FavoriteRadioViewModel::class.java)

        viewModel.apply {
            radioStationsData.observe(this@FavoriteRadioFragment, Observer { mAdapter.radioStationsList = it ?: mutableListOf() })
            loadState.observe(this@FavoriteRadioFragment, Observer {
                when (it) {
                    State.DONE -> mAdapter.notifyDataSetChanged()
                    State.ERROR -> Toast.makeText(context, getString(R.string.cannot_load_radio_list), Toast.LENGTH_SHORT).show()
                    State.LOADING -> {}
                }
            })
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.favorite_radio_layout, container, false)
        view.fav_radio_recycler.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context)
        }

        viewModel.loadRadioListFromDatabase()
        return view
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun updateList(post: String) {
        if (post == "radio") {
            viewModel.loadRadioListFromDatabase()
        }
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mAdapter.unbindService()
    }
}