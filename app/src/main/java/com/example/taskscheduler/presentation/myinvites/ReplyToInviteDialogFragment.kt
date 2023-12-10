package com.example.taskscheduler.presentation.myinvites

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import com.example.taskscheduler.R
import com.example.taskscheduler.domain.models.Invite

class ReplyToInviteDialogFragment : DialogFragment() {

    lateinit var invite: Invite

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        parseArgs()
        Log.i("USER_INVITE_SHOW", invite.boardName)
        return requireActivity().let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(getString(R.string.accept_invite))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.to_accept), object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        (parentFragment as MyInvitesFragment).okClicked(invite)
                    }

                }).setNegativeButton(getString(R.string.to_decline), object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        (parentFragment as MyInvitesFragment).cancelClicked(invite)
                    }
                }).create()
        }

    }

    private fun parseArgs() {
        invite = requireArguments().getParcelable(KEY_INVITE) ?: Invite()
    }

    companion object {

        const val KEY_INVITE = "invite"

        fun newInstance(invite: Invite): ReplyToInviteDialogFragment {
            return ReplyToInviteDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_INVITE, invite)
                }
            }
        }
    }
}