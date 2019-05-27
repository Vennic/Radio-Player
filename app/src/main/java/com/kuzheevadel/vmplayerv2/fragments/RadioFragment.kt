package com.kuzheevadel.vmplayerv2.fragments

import android.annotation.SuppressLint
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.kuzheevadel.vmplayerv2.R
import com.kuzheevadel.vmplayerv2.adapters.RadioStationsAdapter
import com.kuzheevadel.vmplayerv2.dagger.App
import com.kuzheevadel.vmplayerv2.dagger.CustomViewModelFactory
import com.kuzheevadel.vmplayerv2.services.State
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
    private lateinit var loadingState: MutableLiveData<State>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (activity?.application as App).getComponent().inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(RadioViewModel::class.java)
        loadingState = viewModel.loadingState

        viewModel.setAdapter(adapter)
        viewModel.loadRadioStations()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.radio_layout, container, false)
        view.radio_recycler.layoutManager = LinearLayoutManager(context)
        view.radio_recycler.adapter = adapter
        view.radio_reload_button.setOnClickListener { viewModel.loadRadioStations() }

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