package com.example.taskscheduler.presentation.boardupdated

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.taskscheduler.R

class CreateNewListDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_new_list, container, false)
        val editText = view.findViewById<EditText>(R.id.editTextCreateList)
        val buttonSave = view.findViewById<Button>(R.id.buttonCreateList)
        buttonSave.setOnClickListener {
            val listTitle = editText.text.toString().trim()
            if (listTitle.isNotEmpty()) {
                (parentFragment as OuterBoardFragment).createNewList(listTitle)
                Handler(Looper.getMainLooper()).postDelayed({
                    dismiss()
                },1000)
            } else {
                Toast.makeText(context, String.format(getString(R.string.fill_in_the_input_field)), Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }
}