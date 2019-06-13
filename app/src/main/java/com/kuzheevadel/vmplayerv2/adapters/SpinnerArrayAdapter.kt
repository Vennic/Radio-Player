package com.kuzheevadel.vmplayerv2.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.kuzheevadel.vmplayerv2.R
import com.kuzheevadel.vmplayerv2.model.Country

class SpinnerArrayAdapter(context: Context,
                          countryLIst: MutableList<Country>): ArrayAdapter<Country>(context, 0, countryLIst) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    private fun initView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.spinner_country_row, parent, false)
        }

        val item = getItem(position)
        val text = view?.findViewById<TextView>(R.id.spinner_country_text)

        if (item != null) {
             text?.text = item.value
        }

        return view!!
    }
}