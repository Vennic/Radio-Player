package com.kuzheevadel.vmplayerv2.viewmodels

import android.annotation.SuppressLint
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.kuzheevadel.vmplayerv2.model.Country
import com.kuzheevadel.vmplayerv2.model.RadioStation
import com.kuzheevadel.vmplayerv2.paging.RadioDataSourceFactory
import com.kuzheevadel.vmplayerv2.services.VmpNetwork
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SearchRadioViewModel @Inject constructor(private val network: VmpNetwork): ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private lateinit var radioDataSourceFactory: RadioDataSourceFactory
    lateinit var listLiveData: LiveData<PagedList<RadioStation>>
    val countriesData: MutableLiveData<MutableList<Country>> = MutableLiveData()

    fun searchRadioStations(name: String, country: String) {
        radioDataSourceFactory = RadioDataSourceFactory(network, compositeDisposable, name, country)
        initializePaging()
    }

    private fun initializePaging() {
        val pagedListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(50)
            .setPageSize(50)
            .build()

        listLiveData = LivePagedListBuilder(radioDataSourceFactory, pagedListConfig)
            .build()
    }

    @SuppressLint("CheckResult")
    fun loadCountriesList() {
        network.getCountriesList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                it.add(0, Country("All countries", "", ""))
                countriesData.value = it
                },

                {

                })
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}