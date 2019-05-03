package com.kuzheevadel.vmplayerv2.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kuzheevadel.vmplayerv2.R
import com.kuzheevadel.vmplayerv2.adapters.TracksRecyclerAdapter
import com.kuzheevadel.vmplayerv2.interfaces.MvpContracts
import kotlinx.android.synthetic.main.recycler_layout.view.*

class AllTracksFragment: Fragment() {

    private lateinit var tracksRecycler: RecyclerView
    private lateinit var adapter: TracksRecyclerAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.recycler_layout, container, false)

        tracksRecycler = view.recycler_view
        tracksRecycler.layoutManager = LinearLayoutManager(context)
        adapter = TracksRecyclerAdapter(context)
        tracksRecycler.adapter = adapter
        tracksRecycler.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        return view
    }
}