package com.kuzheevadel.vmplayerv2.paging

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import com.kuzheevadel.vmplayerv2.model.RadioStation
import com.kuzheevadel.vmplayerv2.services.VmpNetwork
import io.reactivex.disposables.CompositeDisposable

class RadioDataSourceFactory(private val network: VmpNetwork,
                             private val compositeDisposable: CompositeDisposable,
                             private val name: String,
                             private val country: String): DataSource.Factory<Int, RadioStation>() {

    val liveData: MutableLiveData<RadioDataSource> = MutableLiveData()

    override fun create(): DataSource<Int, RadioStation> {
        val radioDataSource = RadioDataSource(network, compositeDisposable, name,country)
        liveData.postValue(radioDataSource)
        return radioDataSource
    }

}