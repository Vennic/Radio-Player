package com.kuzheevadel.vmplayerv2.adapters

import android.databinding.BindingAdapter
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.net.Uri
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kuzheevadel.vmplayerv2.R
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation

abstract class BaseRecyclerAdapter<T>: RecyclerView.Adapter<BaseRecyclerAdapter<T>.BindingHolder>() {

    private var itemsList = mutableListOf<T>()

    fun setDataList(list: MutableList<T>) {
        itemsList = list
    }

    abstract fun getItemLayoutId(): Int
    abstract fun getVariableId(): Int

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): BindingHolder {
        val view = LayoutInflater.from(parent.context).inflate(getItemLayoutId(), parent, false)
        return BindingHolder(view)
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    override fun onBindViewHolder(viewHolder: BindingHolder, position: Int) {
        val item = itemsList[position]
        viewHolder.getDataBinding()?.setVariable(getVariableId(), item)
        viewHolder.getDataBinding()?.executePendingBindings()
    }

    inner class BindingHolder(view: View): RecyclerView.ViewHolder(view) {

        private var binding: ViewDataBinding? = DataBindingUtil.bind(view)

        fun getDataBinding(): ViewDataBinding? {
            return binding
        }
    }
}

@BindingAdapter(value = ["app:url"])
fun loadRoundedCornersImage(view: AppCompatImageView, uri: Uri) {
    Picasso.get()
        .load(uri)
        .centerCrop()
        .placeholder(R.drawable.vinil_default)
        .resize(100, 100)
        .transform(RoundedCornersTransformation(20, 3))
        .into(view)
}