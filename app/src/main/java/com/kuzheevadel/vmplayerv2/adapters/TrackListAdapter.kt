package com.kuzheevadel.vmplayerv2.adapters

import com.android.databinding.library.baseAdapters.BR
import com.kuzheevadel.vmplayerv2.R
import com.kuzheevadel.vmplayerv2.model.Track

class TrackListAdapter: BaseRecyclerAdapter<Track>() {

    override fun getItemLayoutId(): Int {
        return R.layout.track_item_layout
    }

    override fun getVariableId(): Int {
        return BR.track
    }
}