package com.example.taskscheduler.presentation

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.taskscheduler.domain.Invite
import com.example.taskscheduler.presentation.myinvites.MyInvitesFragment

class ReplyToInviteDialogFragment : DialogFragment() {

    lateinit var invite: Invite

//    private val args by navArgs<ReplyToInviteDialogFragmentArgs>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        parseArgs()
        return requireActivity().let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Принять приглашение?")
                .setCancelable(true)
                .setPositiveButton("Да", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        (parentFragment as MyInvitesFragment).okClicked(invite)
                    }

                }).setNegativeButton("Нет", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        (parentFragment as MyInvitesFragment).cancelClicked()
                    }

                }).create()
        }

    }
    private fun parseArgs() {
        invite = requireArguments().getParcelable<Invite>(KEY_INVITE)!!
//        invite = args.invite
    }

    companion object {
        const val KEY_INVITE = "invite"

        fun newInstance(invite: Invite): ReplyToInviteDialogFragment{
            return ReplyToInviteDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_INVITE, invite)
                }
            }
        }
    }
}