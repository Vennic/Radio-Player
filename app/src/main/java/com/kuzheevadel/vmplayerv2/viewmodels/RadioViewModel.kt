package com.kuzheevadel.vmplayerv2.viewmodels

import android.annotation.SuppressLint
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.kuzheevadel.vmplayerv2.adapters.RadioStationsAdapter
import com.kuzheevadel.vmplayerv2.common.State
import com.kuzheevadel.vmplayerv2.services.VmpNetwork
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class RadioViewModel @Inject constructor(private val network: VmpNetwork): ViewModel() {

    private lateinit var mAdapter: RadioStationsAdapter

    val loadingState: MutableLiveData<State> = MutableLiveData()

    fun setAdapter(adapter: RadioStationsAdapter) {
        mAdapter = adapter
    }

    @SuppressLint("CheckResult")
    fun loadRadioStations() {
        loadingState.value = State.LOADING

        network.getStationListByVote()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    mAdapter.radioStationsList = it
                    loadingState.value = State.DONE
                    mAdapter.notifyDataSetChanged()
                },
                {
                    loadingState.value = State.ERROR
                }
            )
    }
}