package com.example.taskscheduler.presentation.registration

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.taskscheduler.R
import com.example.taskscheduler.presentation.registration.RegistrationFragment.Companion.KEY_BUNDLE_CHOOSE_AVATAR_DIALOG
import com.example.taskscheduler.presentation.registration.RegistrationFragment.Companion.KEY_REQUEST_CHOOSE_AVATAR_DIALOG

class ChooseAvatarOptionFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return requireActivity().let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(getString(R.string.from_where_pick_photo))
                .setCancelable(true)
                .setPositiveButton(
                    getString(R.string.gallery)
                ) { _, _ ->
                    setFragmentResult(KEY_REQUEST_CHOOSE_AVATAR_DIALOG, bundleOf(KEY_BUNDLE_CHOOSE_AVATAR_DIALOG to true))
                }
                .setNegativeButton(
                    getString(R.string.camera)
                ) { _, _ ->
                    setFragmentResult(KEY_REQUEST_CHOOSE_AVATAR_DIALOG, bundleOf(KEY_BUNDLE_CHOOSE_AVATAR_DIALOG to false))
                }
                .create()
        }
    }

    companion object {

        fun newInstance(): ChooseAvatarOptionFragment {

            return ChooseAvatarOptionFragment()
        }
    }
}