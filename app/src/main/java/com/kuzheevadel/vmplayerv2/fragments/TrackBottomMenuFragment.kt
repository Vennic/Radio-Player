package com.kuzheevadel.vmplayerv2.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kuzheevadel.vmplayerv2.R
import com.kuzheevadel.vmplayerv2.dagger.App
import com.kuzheevadel.vmplayerv2.dagger.CustomViewModelFactory
import com.kuzheevadel.vmplayerv2.databinding.BottomMenuDialogBinding
import com.kuzheevadel.vmplayerv2.viewmodels.BottomMenuViewModel
import javax.inject.Inject

class TrackBottomMenuFragment: BottomSheetDialogFragment() {

    @Inject
    lateinit var factory: CustomViewModelFactory
    var position: Int = 0

    private lateinit var viewModel: BottomMenuViewModel
    private lateinit var binding: BottomMenuDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as App).getComponent().inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(BottomMenuViewModel::class.java)
        viewModel.getTrackInfo(position, getString(R.string.kbps))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.bottom_menu_dialog, container, false)

        binding.run {
            bottomAlbumText.isSelected = true
            bottomArtistText.isSelected = true
            bottomTitleText.isSelected = true
        }

        viewModel.trackInfoData.observe(this, Observer {
            binding.trackInfo = it
        })

        return binding.root
    }
}