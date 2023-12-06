package com.example.taskscheduler.presentation.newnote

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.taskscheduler.domain.models.ListOfNotesItem

class ListDialogFragment(val list: List<ListOfNotesItem>) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        return super.onCreateDialog(savedInstanceState)
        val listTitles = ArrayList<String>()
        for (item in list) {
            listTitles.add(item.title)
        }
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Выберите список для перемещения")
        builder.setItems(listTitles.toTypedArray(), object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                (parentFragment as NewNoteFragment).moveNote(list[which])
            }


        })
        return builder.create()
    }
}