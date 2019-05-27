package com.kuzheevadel.vmplayerv2.services

import com.google.gson.GsonBuilder
import com.kuzheevadel.vmplayerv2.common.Constants
import com.kuzheevadel.vmplayerv2.interfaces.Interfaces
import com.kuzheevadel.vmplayerv2.radio.RadioStation
import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    //http://api.dirble.com/v2/stations/popular?token={your token}

    @GET("/v2/stations/")
    fun getAllRadioStations(@Query("page") page: String,
                            @Query("per_page") per_page: String,
                            @Query("token") apiKey: String): Observable<MutableList<RadioStation>>

    @GET("/v2/stations/popular")
    fun getPopularRadioStations(@Query("token") apiKey: String): Observable<MutableList<RadioStation>>


}

class VmpNetwork: Interfaces.Network {

    private val apiKey = "ca22a774a26a15ffb46cdb6c73"

    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_RADIO_URL)
        .client(getOkHttpClient())
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()

    override fun getStationsList(type: Int, page: Int): Observable<MutableList<RadioStation>> {
        return retrofit.create(ApiService::class.java).getPopularRadioStations(apiKey)
    }

    private fun getOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()

        builder.addInterceptor { chain ->
            val requestOriginal = chain.request()
            val originalUrl = requestOriginal.url()
            val url = originalUrl.newBuilder()
                .build()
            val requestBuilder = requestOriginal.newBuilder().url(url)

            return@addInterceptor chain.proceed(requestBuilder.build())
        }

        return builder.build()
    }


}