package com.kuzheevadel.vmplayerv2.fragments

import android.Manifest
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kuzheevadel.vmplayerv2.R
import com.kuzheevadel.vmplayerv2.adapters.TracksRecyclerAdapter
import com.kuzheevadel.vmplayerv2.di.App
import com.kuzheevadel.vmplayerv2.di.CustomViewModelFactory
import com.kuzheevadel.vmplayerv2.viewmodels.AllTracksViewModel
import kotlinx.android.synthetic.main.recycler_layout.view.*
import javax.inject.Inject

class AllTracksFragment: Fragment() {

    private lateinit var tracksRecycler: RecyclerView

    @Inject
    lateinit var viewModel: AllTracksViewModel

    @Inject
    lateinit var factory: CustomViewModelFactory

    @Inject
    lateinit var mAdapter: TracksRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (activity?.application as App).getComponent().inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(AllTracksViewModel::class.java)
        viewModel.setAdapter(mAdapter)

        val permissionStatus = ContextCompat.checkSelfPermission(activity!!.applicationContext,
            Manifest.permission.READ_EXTERNAL_STORAGE)

        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            viewModel.loadTracks()
        }  else {
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                1)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.recycler_layout, container, false)

        tracksRecycler = view.recycler_view
        tracksRecycler.layoutManager = LinearLayoutManager(context)
        tracksRecycler.adapter = mAdapter
        tracksRecycler.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

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

}