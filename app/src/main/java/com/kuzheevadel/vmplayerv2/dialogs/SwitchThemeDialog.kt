package com.kuzheevadel.vmplayerv2.dialogs

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatDialogFragment
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.kuzheevadel.vmplayerv2.R
import com.kuzheevadel.vmplayerv2.common.Constants

class SwitchThemeDialog: AppCompatDialogFragment() {

    private lateinit var pref: SharedPreferences
    private var currentId = Constants.themeId
    private var changedId = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        pref = context.getSharedPreferences("laststate", Context.MODE_PRIVATE)

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context!!)
        val view: View? = activity?.layoutInflater?.inflate(R.layout.switch_theme_dialog_layout, null)
        val radioGroup = view?.findViewById<RadioGroup>(R.id.switch_theme_radio_group)
        val radioButtonDark = view?.findViewById<RadioButton>(R.id.radio_button_dark)
        val radioButtonLight = view?.findViewById<RadioButton>(R.id.radio_button_light)

        when (Constants.themeId) {
            R.style.FeedActivityThemeDark -> {
                radioButtonDark?.isChecked = true
            }

            R.style.FeedActivityThemeLight -> {
                radioButtonLight?.isChecked = true
            }
        }

        radioGroup?.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_button_dark -> {
                        changedId = R.style.FeedActivityThemeDark
                }

                R.id.radio_button_light -> {
                        changedId = R.style.FeedActivityThemeLight
                }
            }
        }

        builder.setView(view)
            .setTitle(getString(R.string.choose_theme))
            .setNegativeButton(getString(R.string.cancel_dialog)) {_, _ ->}
            .setPositiveButton("ok") { _, _ ->
                if (changedId != currentId) {
                    pref.edit()
                        .putInt(Constants.THEME_ID, changedId)
                        .apply()

                    Toast.makeText(context, getString(R.string.restart_app), Toast.LENGTH_SHORT).show()
                }
            }

        return builder.create()
    }
}