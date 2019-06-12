package com.kuzheevadel.vmplayerv2.paging

import android.annotation.SuppressLint
import android.arch.paging.PageKeyedDataSource
import android.util.Log
import com.kuzheevadel.vmplayerv2.model.RadioStation
import com.kuzheevadel.vmplayerv2.services.VmpNetwork
import io.reactivex.disposables.CompositeDisposable

class RadioDataSource(private val network: VmpNetwork,
                      private val compositeDisposable: CompositeDisposable): PageKeyedDataSource<Int, RadioStation>() {

    private var index: Int = 0

    @SuppressLint("CheckResult")
    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, RadioStation>) {
        network.executeRadioApi(index)
            .doOnSubscribe { compositeDisposable.add(it) }
            .subscribe {
                Log.i("PagingTest", it.toString())
                for (i in it) {
                    Log.i("PagingTest", i.favicon)
                }
                index++
                callback.onResult(it, null, index)
            }
    }

    @SuppressLint("CheckResult")
    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, RadioStation>) {
        network.executeRadioApi(params.key)
            .doOnSubscribe { compositeDisposable.add(it) }
            .subscribe {
                callback.onResult(it, params.key + 1)
            }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, RadioStation>) {
    }
}