package com.kuzheevadel.vmplayerv2.paging

import android.annotation.SuppressLint
import android.arch.paging.PageKeyedDataSource
import com.kuzheevadel.vmplayerv2.model.RadioStation
import com.kuzheevadel.vmplayerv2.services.VmpNetwork
import io.reactivex.disposables.CompositeDisposable

class RadioDataSource(private val network: VmpNetwork,
                      private val compositeDisposable: CompositeDisposable,
                      private val name: String,
                      private val country: String): PageKeyedDataSource<Int, RadioStation>() {

    private var index: Int = 0

    @SuppressLint("CheckResult")
    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, RadioStation>) {
        network.executeRadioApi(index, name, country)
            .doOnSubscribe { compositeDisposable.add(it) }
            .subscribe {
                index += 50
                callback.onResult(it, null, index)
            }
    }

    @SuppressLint("CheckResult")
    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, RadioStation>) {
        network.executeRadioApi(params.key, name, country)
            .doOnSubscribe { compositeDisposable.add(it) }
            .subscribe {
                callback.onResult(it, params.key + 50)
            }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, RadioStation>) {
    }
}