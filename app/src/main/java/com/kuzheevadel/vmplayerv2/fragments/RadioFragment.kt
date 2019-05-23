package com.kuzheevadel.vmplayerv2.fragments

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kuzheevadel.vmplayerv2.R
import com.kuzheevadel.vmplayerv2.adapters.RadioStationsAdapter
import com.kuzheevadel.vmplayerv2.dagger.App
import com.kuzheevadel.vmplayerv2.dagger.CustomViewModelFactory
import com.kuzheevadel.vmplayerv2.services.VmpNetwork
import com.kuzheevadel.vmplayerv2.viewmodels.AllTracksViewModel
import com.kuzheevadel.vmplayerv2.viewmodels.RadioViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.radio_layout.view.*
import javax.inject.Inject

class RadioFragment: Fragment() {

    @Inject
    lateinit var factory: CustomViewModelFactory

    @Inject
    lateinit var adapter: RadioStationsAdapter

    private lateinit var viewModel: RadioViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (activity?.application as App).getComponent().inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(RadioViewModel::class.java)
        viewModel.setAdapter(adapter)
        viewModel.loadRadioStations()
        /*val net = VmpNetwork()

        net.getStationsList(1, 1)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe {
                Log.i("NetworkTest", it.toString())
            }*/
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.radio_layout, container, false)
        view.radio_recycler.layoutManager = LinearLayoutManager(context)
        view.radio_recycler.adapter = adapter
        return view
    }
}