package com.kuzheevadel.vmplayerv2.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.kuzheevadel.vmplayerv2.R
import com.kuzheevadel.vmplayerv2.adapters.RadioStationsAdapter
import com.kuzheevadel.vmplayerv2.common.State
import com.kuzheevadel.vmplayerv2.dagger.App
import com.kuzheevadel.vmplayerv2.dagger.CustomViewModelFactory
import com.kuzheevadel.vmplayerv2.viewmodels.FavoriteRadioViewModel
import kotlinx.android.synthetic.main.favorite_radio_layout.view.*
import javax.inject.Inject

class FavoriteRadioFragment: Fragment() {

    @Inject
    lateinit var mAdapter: RadioStationsAdapter

    @Inject
    lateinit var mFactory: CustomViewModelFactory

    private lateinit var viewModel: FavoriteRadioViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("LifecycleTest", "onCreate")

        (activity?.application as App).getComponent().inject(this)
        viewModel = ViewModelProviders.of(this, mFactory).get(FavoriteRadioViewModel::class.java)

        viewModel.apply {
            radioStationsData.observe(this@FavoriteRadioFragment, Observer { mAdapter.radioStationsList = it ?: mutableListOf() })
            loadState.observe(this@FavoriteRadioFragment, Observer {
                when (it) {
                    State.DONE -> mAdapter.notifyDataSetChanged()
                    State.ERROR -> Toast.makeText(context, "Cannot load radio playlist", Toast.LENGTH_SHORT).show()

                }
            })
        }

        //viewModel.loadRadioListFromDatabase()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.i("LifecycleTest", "onCreateView")
        val view = inflater.inflate(R.layout.favorite_radio_layout, container, false)
        view.fav_radio_recycler.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context)
        }

        viewModel.loadRadioListFromDatabase()
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Log.i("LifecycleTest", "onAttach")
    }

    override fun onStart() {
        super.onStart()
        Log.i("LifecycleTest", "onStart")
    }

    override fun onStop() {
        super.onStop()
        Log.i("LifecycleTest", "onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i("LifecycleTest", "onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        mAdapter.unbindService()
        Log.i("LifecycleTest", "onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        Log.i("LifecycleTest", "onDetach")
    }

    override fun onPause() {
        super.onPause()
        Log.i("LifecycleTest", "onPause")
    }

    override fun onResume() {
        super.onResume()
        Log.i("LifecycleTest", "onResume")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("LifecycleTest", "onViewCreated")
    }

}