package com.example.taskscheduler

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.taskscheduler.presentation.registration.RegistrationFragment

class ChooseAvatarOptionFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return requireActivity().let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Откуда возьмем фото?")
                .setCancelable(true)
                .setPositiveButton("Галлерея", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        (parentFragment as RegistrationFragment).galleryClicked()
                    }

                }).setNegativeButton("Камера", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        (parentFragment as RegistrationFragment).cameraClicked()
                    }

                }).create()
        }
    }

    companion object {

        fun newInstance(): ChooseAvatarOptionFragment {

            return ChooseAvatarOptionFragment()
        }
    }

}