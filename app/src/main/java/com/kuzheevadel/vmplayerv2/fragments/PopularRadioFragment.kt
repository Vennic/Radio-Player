package com.kuzheevadel.vmplayerv2.fragments

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kuzheevadel.vmplayerv2.R
import com.kuzheevadel.vmplayerv2.adapters.RadioStationsAdapter
import com.kuzheevadel.vmplayerv2.dagger.App
import com.kuzheevadel.vmplayerv2.dagger.CustomViewModelFactory
import com.kuzheevadel.vmplayerv2.services.State
import com.kuzheevadel.vmplayerv2.viewmodels.RadioViewModel
import kotlinx.android.synthetic.main.popular_radio_layout.view.*
import javax.inject.Inject

class PopularRadioFragment: Fragment() {

    @Inject
    lateinit var mAdapter: RadioStationsAdapter

    @Inject
    lateinit var factory: CustomViewModelFactory

    private lateinit var viewmodel: RadioViewModel
    private lateinit var loadingState: MutableLiveData<State>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (activity?.application as App).getComponent().inject(this)
        viewmodel = ViewModelProviders.of(this, factory).get(RadioViewModel::class.java)
        loadingState = viewmodel.loadingState
        viewmodel.setAdapter(mAdapter)

        viewmodel.loadRadioStations()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.popular_radio_layout, container, false)

        view.popular_radio_recycler.layoutManager = LinearLayoutManager(context)
        view.popular_radio_recycler.adapter = mAdapter
        view.radio_reload_button.setOnClickListener { viewmodel.loadRadioStations() }


        loadingState.observe(this, Observer {
            when (it) {
                State.LOADING -> {
                    view.radio_progressBar.visibility = View.VISIBLE
                    view.radio_reload_button.visibility = View.GONE
                }

                State.DONE -> view.radio_progressBar.visibility = View.GONE


                State.ERROR -> {
                    view.radio_progressBar.visibility = View.GONE
                    view.radio_reload_button.visibility = View.VISIBLE
                    Snackbar.make(view, "Server is not available", Snackbar.LENGTH_LONG).show()
                }
            }
        })
        return view
    }
}