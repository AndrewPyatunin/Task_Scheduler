package com.example.taskscheduler.presentation.newnote

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.taskscheduler.R
import com.example.taskscheduler.domain.models.NotesListItem
import com.example.taskscheduler.presentation.newnote.NewNoteFragment.Companion.KEY_BUNDLE_NOTES_LIST_DIALOG
import com.example.taskscheduler.presentation.newnote.NewNoteFragment.Companion.KEY_REQUEST_NOTES_LIST_DIALOG

class ListDialogFragment(val list: List<NotesListItem>) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val listTitles = ArrayList<String>()
        list.forEach {
            listTitles.add(it.title)
        }
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.choose_the_list))
        builder.setItems(listTitles.toTypedArray()
        ) { _, which ->
            setFragmentResult(KEY_REQUEST_NOTES_LIST_DIALOG, bundleOf(KEY_BUNDLE_NOTES_LIST_DIALOG to which))
        }
        return builder.create()
    }
}