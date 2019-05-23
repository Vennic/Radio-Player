package com.kuzheevadel.vmplayerv2.adapters

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kuzheevadel.vmplayerv2.BR
import com.kuzheevadel.vmplayerv2.databinding.RadioStationItemBinding
import com.kuzheevadel.vmplayerv2.radio.RadioStation

class RadioStationsAdapter: RecyclerView.Adapter<RadioStationsAdapter.RadioViewHolder>() {

    var radioStationsList = mutableListOf<RadioStation>()

    override fun onCreateViewHolder(parent: ViewGroup, pos: Int): RadioViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RadioStationItemBinding.inflate(inflater, parent, false)
        return RadioViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return radioStationsList.size
    }

    override fun onBindViewHolder(viewHolder: RadioViewHolder, pos: Int) {
        val radioStation = radioStationsList[pos]
        viewHolder.binding?.setVariable(BR.radioStation, radioStation)
        viewHolder.binding?.executePendingBindings()

    }

    inner class RadioViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val binding: RadioStationItemBinding? = DataBindingUtil.bind(view)
    }
}