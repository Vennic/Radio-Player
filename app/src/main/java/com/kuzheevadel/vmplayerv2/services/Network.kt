package com.kuzheevadel.vmplayerv2.services

import com.google.gson.GsonBuilder
import com.kuzheevadel.vmplayerv2.common.Constants
import com.kuzheevadel.vmplayerv2.model.Country
import com.kuzheevadel.vmplayerv2.model.RadioStation
import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("/webservice/json/countries")
    fun getCountriesList(): Observable<MutableList<Country>>

    @GET("/webservice/json/stations/topvote/100")
    fun getStationsByVote(): Observable<MutableList<RadioStation>>

    @GET("/webservice/json/stations/search")
    fun getSearchStations(@Query("name") name: String,
                          @Query("country") country: String,
                          @Query("limit") limit: String,
                          @Query("offset") offset: String): Observable<MutableList<RadioStation>>


}

class VmpNetwork {

    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_RADIO_URL)
        .client(getOkHttpClient())
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(ApiService::class.java)

    fun executeRadioApi(index: Int, name: String, country: String): Observable<MutableList<RadioStation>> {
        return retrofit.getSearchStations(name, country, "50", index.toString())
    }

    fun getCountriesList(): Observable<MutableList<Country>> {
        return retrofit.getCountriesList()
    }

    fun getStationListByVote(): Observable<MutableList<RadioStation>> {
        return retrofit.getStationsByVote()
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