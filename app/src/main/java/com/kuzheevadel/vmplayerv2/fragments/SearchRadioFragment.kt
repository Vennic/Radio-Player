package com.kuzheevadel.vmplayerv2.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.kuzheevadel.vmplayerv2.R
import com.kuzheevadel.vmplayerv2.common.Constants
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
    private var searchText = ""

    @Inject
    lateinit var mAdapter: RadioPagingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState?.getString(Constants.SEARCH_TEXT) != null) {
            searchText = savedInstanceState.getString(Constants.SEARCH_TEXT) ?: ""
        }

        (activity?.application as App).getComponent().inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(SearchRadioViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.search_radio_layout, container, false)
        view.search_radio_recycler.layoutManager = LinearLayoutManager(context)
        view.search_radio_recycler.adapter = mAdapter

        view.search_radio_edit_text.setOnKeyListener { _, keyCode, event ->
            if(event.action == KeyEvent.ACTION_DOWN &&
                (keyCode == KeyEvent.KEYCODE_ENTER)) {

                if (view.search_radio_edit_text.text.toString() != "") {

                    searchText = view.search_radio_edit_text.text.toString()
                    viewModel.searchRadioStations(searchText)
                    viewModel.listLiveData.observe(this, Observer { mAdapter.submitList(it) })
                    return@setOnKeyListener true
                } else {
                    Toast.makeText(context, "Empty search", Toast.LENGTH_SHORT).show()
                    return@setOnKeyListener true
                }
            }
            return@setOnKeyListener false
        }

        initialList()

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (searchText != "") {
            outState.putString(Constants.SEARCH_TEXT, searchText)
        }
        super.onSaveInstanceState(outState)
    }

    private fun initialList() {
        if (searchText != "") {
            viewModel.searchRadioStations(searchText)
            viewModel.listLiveData.observe(this, Observer { mAdapter.submitList(it) })
        }
    }

}