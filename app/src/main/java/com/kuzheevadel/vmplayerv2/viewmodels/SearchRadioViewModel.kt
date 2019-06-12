package com.kuzheevadel.vmplayerv2.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.kuzheevadel.vmplayerv2.model.RadioStation
import com.kuzheevadel.vmplayerv2.paging.RadioDataSourceFactory
import com.kuzheevadel.vmplayerv2.services.VmpNetwork
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class SearchRadioViewModel @Inject constructor( network: VmpNetwork): ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val radioDataSourceFactory = RadioDataSourceFactory(network, compositeDisposable)
    lateinit var listLiveData: LiveData<PagedList<RadioStation>>

    init {
        initializePaging()
    }


    private fun initializePaging() {
        val pagedListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(40)
            .setPageSize(40)
            .build()

        listLiveData = LivePagedListBuilder(radioDataSourceFactory, pagedListConfig)
            .build()
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}