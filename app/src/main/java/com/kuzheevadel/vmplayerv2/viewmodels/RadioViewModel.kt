package com.kuzheevadel.vmplayerv2.viewmodels

import android.arch.lifecycle.ViewModel
import com.kuzheevadel.vmplayerv2.adapters.RadioStationsAdapter
import com.kuzheevadel.vmplayerv2.interfaces.Interfaces
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class RadioViewModel @Inject constructor(private val network: Interfaces.Network): ViewModel() {

    private lateinit var mAdapter: RadioStationsAdapter

    fun setAdapter(adapter: RadioStationsAdapter) {
        mAdapter = adapter
    }

    fun loadRadioStations() {
        network.getStationsList(1, 1)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe {
                mAdapter.radioStationsList = it
                mAdapter.notifyDataSetChanged()
            }
    }
}