package com.example.taskscheduler.presentation.boardupdated

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.taskscheduler.R
import com.example.taskscheduler.presentation.boardupdated.OuterBoardFragment.Companion.KEY_BUNDLE_DIALOG
import com.example.taskscheduler.presentation.boardupdated.OuterBoardFragment.Companion.KEY_REQUEST_DIALOG

class CreateNewListDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_new_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val editText = view.findViewById<EditText>(R.id.editTextCreateList)
        val buttonSave = view.findViewById<Button>(R.id.buttonCreateList)
        buttonSave?.setOnClickListener {
            val listTitle = editText.text.toString().trim()
            if (listTitle.isNotEmpty()) {
//                (parentFragment as OuterBoardFragment).createNewList(listTitle)
                setFragmentResult(KEY_REQUEST_DIALOG, bundleOf(KEY_BUNDLE_DIALOG to listTitle))
                dismiss()
            } else {
                Toast.makeText(
                    context,
                    String.format(getString(R.string.fill_in_the_input_field)),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}