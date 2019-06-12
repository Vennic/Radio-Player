package com.kuzheevadel.vmplayerv2.services

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PageKeyedDataSource
import com.google.gson.GsonBuilder
import com.kuzheevadel.vmplayerv2.common.Constants
import com.kuzheevadel.vmplayerv2.radio.RadioStationDirble
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkService {

    //http://api.dirble.com/v2/stations/popular?token={your token}

    @GET("/v2/stations/")
    fun getAllRadioStations(@Query("page") page: String,
                            @Query("per_page") per_page: String,
                            @Query("token") apiKey: String): Observable<MutableList<RadioStationDirble>>

    @GET("/v2/stations/popular")
    fun getPopularRadioStations(@Query("page") page: Int,
                                @Query("per_page") per_page: Int,
                                @Query("token") apiKey: String): Observable<MutableList<RadioStationDirble>>

    companion object {
        fun getService(): NetworkService {
            val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_RADIO_URL)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

            return retrofit.create(NetworkService::class.java)
        }
    }
}

enum class State {
    DONE, LOADING, ERROR
}

class RadioStationsDataSource(private val networkService: NetworkService,
                              private val compositeDisposable: CompositeDisposable): PageKeyedDataSource<Int, RadioStationDirble>() {

    var state: MutableLiveData<State> = MutableLiveData()
    private var retryCompletable: Completable? = null

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, RadioStationDirble>) {
        updateState(State.LOADING)

        compositeDisposable.add(
            networkService.getPopularRadioStations(1, params.requestedLoadSize, apiKey)
                .subscribe ({
                    updateState(State.DONE)
                    callback.onResult(it, null, 2)
                },
                    {
                        updateState(State.ERROR)
                        setRetry(Action { loadInitial(params, callback) })
                    }
                )
        )
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, RadioStationDirble>) {
        updateState(State.LOADING)

        compositeDisposable.add(
            networkService.getPopularRadioStations(params.key, params.requestedLoadSize, apiKey)
                .subscribe(
                    { response ->
                        updateState(State.DONE)
                        callback.onResult(response,
                            params.key + 1
                        )
                    },
                    {
                        updateState(State.ERROR)
                        setRetry(Action { loadAfter(params, callback) })
                    }
                )
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, RadioStationDirble>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun updateState(state: State) {
        this.state.postValue(state)
    }

    fun retry() {
        if (retryCompletable != null) {
            compositeDisposable.add(retryCompletable!!.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe())
        }
    }

    private fun setRetry(action: Action?) {
        retryCompletable = if (action == null) null else Completable.fromAction(action)
    }

    private val apiKey = "ca22a774a26a15ffb46cdb6c73"


}