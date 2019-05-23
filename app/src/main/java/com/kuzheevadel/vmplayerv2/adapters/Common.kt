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
import com.kuzheevadel.vmplayerv2.databinding.AlbumItemBinding
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation

interface ClickHandler {
    fun click(view: View)
}

@BindingAdapter(value = ["app:url"])
fun loadRoundedCornersImage(view: AppCompatImageView, uri: Uri?) {
    Picasso.get()
        .load(uri)
        .centerCrop()
        .placeholder(R.drawable.vinil_default)
        .resize(100, 100)
        .transform(RoundedCornersTransformation(20, 3))
        .into(view)
}

@BindingAdapter(value = ["app:album_url"])
fun loadImage(view: AppCompatImageView, uri: Uri?) {
    Picasso.get().load(uri)
        .fit()
        .placeholder(R.drawable.vinil_default)
        .into(view)
}

@BindingAdapter(value = ["app:radio_thumb"])
fun loadRadioImage(view: AppCompatImageView, url: String?) {
    Picasso.get().load(url)
        .centerCrop()
        .placeholder(R.drawable.vinil_default)
        .resize(100, 100)
        .transform(RoundedCornersTransformation(20, 3))
        .into(view)
}