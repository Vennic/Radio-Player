package com.kuzheevadel.vmplayerv2.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatDialogFragment
import android.view.View
import com.kuzheevadel.vmplayerv2.R
import com.kuzheevadel.vmplayerv2.common.Constants

class PermissionDialogFragment: AppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context!!)
        val view: View? = activity?.layoutInflater?.inflate(R.layout.permission_dialog_layout, null)

        builder.setView(view)
            .setPositiveButton("ok") { _, _ ->
                val intent = Intent()
                intent.putExtra(Constants.PERM_DIALOG_TAG, 1)
                targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
            }

        return builder.create()
    }
}