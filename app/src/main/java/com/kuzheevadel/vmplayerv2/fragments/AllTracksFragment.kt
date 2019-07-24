package com.kuzheevadel.vmplayerv2.fragments

import android.Manifest
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kuzheevadel.vmplayerv2.R
import com.kuzheevadel.vmplayerv2.adapters.TrackListAdapter
import com.kuzheevadel.vmplayerv2.common.State
import com.kuzheevadel.vmplayerv2.dagger.App
import com.kuzheevadel.vmplayerv2.dagger.CustomViewModelFactory
import com.kuzheevadel.vmplayerv2.viewmodels.AllTracksViewModel
import kotlinx.android.synthetic.main.recycler_layout.view.*
import javax.inject.Inject

class AllTracksFragment: Fragment() {

    private lateinit var tracksRecycler: RecyclerView

    private lateinit var viewModel: AllTracksViewModel

    @Inject
    lateinit var factory: CustomViewModelFactory

    @Inject
    lateinit var mAdapter: TrackListAdapter

    lateinit var loadStateData: MutableLiveData<State>

    companion object {
        fun getInstance(liveData: MutableLiveData<State>): AllTracksFragment {
            val instance = AllTracksFragment()
            instance.loadStateData = liveData
            return instance
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (activity?.application as App).getComponent().inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(AllTracksViewModel::class.java)

        viewModel.loadStateData.observe(this, Observer {
            when (it) {
                State.LOADING -> loadStateData.value = State.LOADING
                State.DONE -> loadStateData.value = State.DONE
                State.ERROR -> loadStateData.value = State.ERROR
            }
        })

        viewModel.setAdapter(mAdapter)
        mAdapter.fm = activity?.supportFragmentManager

        val permissionStatus = ContextCompat.checkSelfPermission(activity!!.applicationContext,
            Manifest.permission.READ_EXTERNAL_STORAGE)

        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            viewModel.loadTracks()
        }  else {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.recycler_layout, container, false)

        tracksRecycler = view.recycler_view
        tracksRecycler.layoutManager = LinearLayoutManager(context)
        tracksRecycler.adapter = mAdapter

        return view
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                viewModel.loadTracks()
            } else {
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onDestroy() {
        mAdapter.unbindService()
        super.onDestroy()
    }

}