package com.example.taskscheduler.presentation.newnote

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.taskscheduler.R
import com.example.taskscheduler.domain.models.NotesListItem

class ListDialogFragment(val list: List<NotesListItem>) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val listTitles = ArrayList<String>()
        list.forEach {
            listTitles.add(it.title)
        }
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.choose_the_list))
        builder.setItems(listTitles.toTypedArray()
        ) { _, which -> (parentFragment as NewNoteFragment).moveNote(list[which]) }
        return builder.create()
    }
}