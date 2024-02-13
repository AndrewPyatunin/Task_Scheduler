package com.example.taskscheduler.presentation.myinvites

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.taskscheduler.R
import com.example.taskscheduler.domain.models.Invite

class ReplyToInviteDialogFragment : DialogFragment() {

    lateinit var invite: Invite

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        parseArgs()
        return requireActivity().let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(getString(R.string.accept_invite))
                .setCancelable(true)
                .setPositiveButton(
                    getString(R.string.to_accept)
                ) { _, _ -> (parentFragment as MyInvitesFragment).okClicked(invite) }
                .setNegativeButton(getString(R.string.to_decline)) { _, _ ->
                    (parentFragment as MyInvitesFragment).cancelClicked(invite)
                }.create()
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