package com.kuzheevadel.vmplayerv2.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.kuzheevadel.vmplayerv2.R
import kotlinx.android.synthetic.main.bottom_menu_dialog.view.*

class TrackBottomMenu: BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_menu_dialog, container, false)
        view.bottom_button.setOnClickListener {
            Toast.makeText(context, "Bottom dialog", Toast.LENGTH_SHORT).show()
        }
        return view
    }
}