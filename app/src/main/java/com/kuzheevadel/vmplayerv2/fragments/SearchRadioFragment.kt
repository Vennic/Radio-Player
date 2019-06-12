package com.kuzheevadel.vmplayerv2.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kuzheevadel.vmplayerv2.R
import com.kuzheevadel.vmplayerv2.dagger.App
import com.kuzheevadel.vmplayerv2.dagger.CustomViewModelFactory
import com.kuzheevadel.vmplayerv2.paging.RadioPagingAdapter
import com.kuzheevadel.vmplayerv2.viewmodels.SearchRadioViewModel
import kotlinx.android.synthetic.main.search_radio_layout.view.*
import javax.inject.Inject

class SearchRadioFragment: Fragment() {

    @Inject
    lateinit var factory: CustomViewModelFactory
    private lateinit var viewModel: SearchRadioViewModel

    private val mAdapter = RadioPagingAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as App).getComponent().inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(SearchRadioViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.search_radio_layout, container, false)
        view.search_radio_recycler.layoutManager = LinearLayoutManager(context)
        view.search_radio_recycler.adapter = mAdapter

        viewModel.listLiveData.observe(this, Observer { mAdapter.submitList(it) })
        return view
    }

}