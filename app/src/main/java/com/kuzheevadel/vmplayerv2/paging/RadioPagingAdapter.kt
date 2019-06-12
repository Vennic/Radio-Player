package com.kuzheevadel.vmplayerv2.paging

import android.arch.paging.PagedListAdapter
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kuzheevadel.vmplayerv2.BR
import com.kuzheevadel.vmplayerv2.databinding.RadioStationItemBinding
import com.kuzheevadel.vmplayerv2.model.RadioStation

class RadioPagingAdapter: PagedListAdapter<RadioStation, RadioPagingAdapter.RadioViewHolder>(RadioStation.diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RadioViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RadioStationItemBinding.inflate(inflater, parent, false)
        return RadioViewHolder(binding.root)
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun onBindViewHolder(viewHolder: RadioViewHolder, position: Int) {
        val radioStation = getItem(position)
        //val bundle = Bundle()
        //bundle.putString(Constants.RADIO_URL, radioStation.url)

        viewHolder.binding?.setVariable(BR.radioStation, radioStation)

        /*viewHolder.view.setOnClickListener {
            bindServiceHelper.mediaControllerCompat?.transportControls?.prepareFromMediaId(Constants.RADIO_STATION, bundle)
        }*/

        viewHolder.binding?.executePendingBindings()
    }

    inner class RadioViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val binding: RadioStationItemBinding? = DataBindingUtil.bind(view)
    }
}