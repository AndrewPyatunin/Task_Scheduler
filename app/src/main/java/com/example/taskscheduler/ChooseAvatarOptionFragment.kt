package com.example.taskscheduler

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.taskscheduler.presentation.registration.RegistrationFragment

class ChooseAvatarOptionFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return requireActivity().let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(getString(R.string.from_where_pick_photo))
                .setCancelable(true)
                .setPositiveButton(
                    getString(R.string.gallery)
                ) { dialog, which -> (parentFragment as RegistrationFragment).galleryClicked() }
                .setNegativeButton(
                    getString(R.string.camera)
                ) { dialog, which -> (parentFragment as RegistrationFragment).cameraClicked() }
                .create()
        }
    }

    companion object {

        fun newInstance(): ChooseAvatarOptionFragment {

            return ChooseAvatarOptionFragment()
        }
    }

}