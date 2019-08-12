package com.kuzheevadel.vmplayerv2.fragments

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Toast
import com.kuzheevadel.vmplayerv2.R
import com.kuzheevadel.vmplayerv2.adapters.SpinnerArrayAdapter
import com.kuzheevadel.vmplayerv2.dagger.App
import com.kuzheevadel.vmplayerv2.dagger.CustomViewModelFactory
import com.kuzheevadel.vmplayerv2.helper.ConnectivityHelper
import com.kuzheevadel.vmplayerv2.model.Country
import com.kuzheevadel.vmplayerv2.model.RadioStation
import com.kuzheevadel.vmplayerv2.paging.RadioPagingAdapter
import com.kuzheevadel.vmplayerv2.viewmodels.SearchRadioViewModel
import kotlinx.android.synthetic.main.search_radio_layout.view.*
import javax.inject.Inject

class SearchRadioFragment: Fragment() {

    @Inject
    lateinit var factory: CustomViewModelFactory
    private lateinit var viewModel: SearchRadioViewModel
    private var searchText = ""
    private var countrySearch = ""
    private var currentSpinnerPosition = 0
    private var countriesList: MutableList<Country>? = mutableListOf()
    private lateinit var searchView: View
    private var spinnerAdapter: SpinnerArrayAdapter? = null

    @Inject
    lateinit var mAdapter: RadioPagingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

        (activity?.application as App).getComponent().inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(SearchRadioViewModel::class.java)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        searchView = inflater.inflate(R.layout.search_radio_layout, container, false)
        initializeSpinner(searchView)

        searchView.search_radio_recycler.layoutManager = LinearLayoutManager(context)
        searchView.search_radio_recycler.adapter = mAdapter

        searchView.search_radio_edit_text.editText?.setOnKeyListener { _, keyCode, event ->
            if(event.action == KeyEvent.ACTION_DOWN &&
                (keyCode == KeyEvent.KEYCODE_ENTER)) {

                searchText = searchView.search_radio_edit_text.editText?.text.toString()
                viewModel.searchRadioStations(searchText, countrySearch)
                viewModel.listLiveData.removeObservers (this@SearchRadioFragment )
                viewModel.listLiveData.observe(this@SearchRadioFragment, Observer {
                    mAdapter.submitList(it)
                    setUi(it)
                })
                hideSoftKeyboard(activity)
                return@setOnKeyListener true

            }
            return@setOnKeyListener false
        }

        searchView.search_country_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val country = parent?.getItemAtPosition(position) as Country
                countrySearch = country.value

                currentSpinnerPosition = position

                viewModel.searchRadioStations(searchText, countrySearch)
                viewModel.listLiveData.removeObservers (this@SearchRadioFragment )
                viewModel.listLiveData.observe(this@SearchRadioFragment, Observer {
                    mAdapter.submitList(it)
                    setUi(it)
                })

            }

        }

        return searchView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (ConnectivityHelper.isConnectedToNetwork(context)) {
            viewModel.loadCountriesList(getString(R.string.countries))
            initialList()
        }else {
            Toast.makeText(context, getString(R.string.check_network), Toast.LENGTH_SHORT).show()
        }
    }

    private fun initializeSpinner(view: View) {
        viewModel.countriesData.observe(this, Observer {
            countriesList = it
            if (spinnerAdapter == null) {
                spinnerAdapter = SpinnerArrayAdapter(context!!, countriesList!!)
            }
            view.search_country_spinner.adapter = spinnerAdapter
            view.search_country_spinner.setSelection(currentSpinnerPosition)

        })
    }

    private fun initialList() {
        viewModel.searchRadioStations(searchText, countrySearch)
        viewModel.listLiveData.observe(this, Observer {
            mAdapter.submitList(it)
            setUi(it)
        })
    }

    private fun setUi(list: MutableList<RadioStation>?) {
        if (list?.size == 0) {
            searchView.nothing_found_text.visibility = View.VISIBLE
        } else {
            searchView.nothing_found_text.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        mAdapter.unbindService()
        super.onDestroy()
    }

    private fun hideSoftKeyboard(activityCompat: FragmentActivity?) {
        val inputMethodManager = activityCompat?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(activityCompat.currentFocus?.windowToken, 0)
    }

}