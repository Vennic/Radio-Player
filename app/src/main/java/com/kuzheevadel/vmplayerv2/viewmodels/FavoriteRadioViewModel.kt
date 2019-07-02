package com.kuzheevadel.vmplayerv2.viewmodels

import android.annotation.SuppressLint
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.kuzheevadel.vmplayerv2.common.RadioMessage
import com.kuzheevadel.vmplayerv2.common.State
import com.kuzheevadel.vmplayerv2.database.RadioDatabase
import com.kuzheevadel.vmplayerv2.model.RadioStation
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.Callable
import javax.inject.Inject

class FavoriteRadioViewModel @Inject constructor(private val radioDatabase: RadioDatabase): ViewModel() {

    private val radioDao = radioDatabase.radioDao()
    val radioStationsData: MutableLiveData<MutableList<RadioStation>> = MutableLiveData()
    val loadState: MutableLiveData<State> = MutableLiveData()

    @SuppressLint("CheckResult")
    fun loadRadioListFromDatabase() {
        val callable = Callable<MutableList<RadioStation>> { radioDao.getAllTracks() }

        Log.i("LifecycleTest", "load")

        Observable.fromCallable(callable)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                radioStationsData.value = it
                loadState.value = State.DONE
            },
                {
                    Log.e("PLAYLISTERROR", "", it)
                    loadState.value = State.ERROR
                })
    }

}