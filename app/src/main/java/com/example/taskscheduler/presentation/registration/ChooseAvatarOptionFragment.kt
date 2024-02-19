package com.example.taskscheduler.presentation.registration

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.taskscheduler.R

class ChooseAvatarOptionFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return requireActivity().let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(getString(R.string.from_where_pick_photo))
                .setCancelable(true)
                .setPositiveButton(
                    getString(R.string.gallery)
                ) { _, _ -> (parentFragment as RegistrationFragment).pickImageFromGallery() }
                .setNegativeButton(
                    getString(R.string.camera)
                ) { _, _ -> (parentFragment as RegistrationFragment).takePhotoFromCamera() }
                .create()
        }
    }

    companion object {

        fun newInstance(): ChooseAvatarOptionFragment {

            return ChooseAvatarOptionFragment()
        }
    }
}