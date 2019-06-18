package com.kuzheevadel.vmplayerv2.paging

import android.arch.paging.PagedListAdapter
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.media.session.MediaControllerCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kuzheevadel.vmplayerv2.BR
import com.kuzheevadel.vmplayerv2.bindhelper.BindServiceHelper
import com.kuzheevadel.vmplayerv2.common.Constants
import com.kuzheevadel.vmplayerv2.databinding.RadioStationItemBinding
import com.kuzheevadel.vmplayerv2.model.RadioStation
import com.kuzheevadel.vmplayerv2.repository.RadioRepository

class RadioPagingAdapter(private val bindServiceHelper: BindServiceHelper,
                         private val radioRepository: RadioRepository): PagedListAdapter<RadioStation, RadioPagingAdapter.RadioViewHolder>(RadioStation.diffCallback) {

    init {
        val callback = object : MediaControllerCompat.Callback() {

        }

        bindServiceHelper.bindPlayerService(callback)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RadioViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RadioStationItemBinding.inflate(inflater, parent, false)
        return RadioViewHolder(binding.root)
    }

    override fun onBindViewHolder(viewHolder: RadioViewHolder, position: Int) {
        val radioStation = getItem(position)
        val bundle = Bundle()
        bundle.putString(Constants.RADIO_URL, radioStation?.url)
        bundle.putString(Constants.RADIO_TITLE, radioStation?.name)
        bundle.putString(Constants.RADIO_IMAGE, radioStation?.favicon)

        viewHolder.binding?.setVariable(BR.radioStation, radioStation)

        viewHolder.view.setOnClickListener {
            radioRepository.currentPlayingStation = radioStation
            bindServiceHelper.mediaControllerCompat?.transportControls?.prepareFromMediaId(Constants.RADIO_STATION, bundle)
        }

        viewHolder.binding?.executePendingBindings()
    }

    inner class RadioViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val binding: RadioStationItemBinding? = DataBindingUtil.bind(view)
    }
}