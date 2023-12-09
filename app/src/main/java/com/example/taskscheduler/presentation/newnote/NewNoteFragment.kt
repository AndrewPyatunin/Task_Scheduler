package com.example.taskscheduler.presentation.newnote

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskscheduler.MyDatabaseConnection
import com.example.taskscheduler.MyDatabaseConnection.updated
import com.example.taskscheduler.R
import com.example.taskscheduler.databinding.FragmentNewNoteBinding
import com.example.taskscheduler.domain.*
import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.ListOfNotesItem
import com.example.taskscheduler.domain.models.Note
import com.example.taskscheduler.domain.models.User
import kotlin.math.roundToInt

class NewNoteFragment : Fragment(), MenuProvider {
    lateinit var binding: FragmentNewNoteBinding
    lateinit var listOfNotesItem: ListOfNotesItem
    lateinit var board: Board
    lateinit var user: User
    lateinit var note: Note
    lateinit var viewModel: NewNoteViewModel
    lateinit var newNoteAdapter: NewNoteCheckItemAdapter
    lateinit var recyclerView: RecyclerView
    lateinit var listOfLists: Array<ListOfNotesItem>
    var list = ArrayList<String>()

    private val args by navArgs<NewNoteFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewNoteBinding.inflate(inflater, container, false)
        binding.calendarView.visibility = View.GONE
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArgs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[NewNoteViewModel::class.java]
        MyDatabaseConnection.updated = false
        observeViewModel()
        binding.cardNewNoteDescription.background.alpha = 0
        binding.newNoteCard.background.alpha = 0
        initViews()
        if (note.id != "") {
            val menuHost: MenuHost = requireActivity()
            menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
            with(binding) {
                newNoteTitle.visibility = View.GONE
                editTextDescription.visibility = View.GONE
                textViewDescription.text = note.description
                textViewNewNoteTitle.text = note.title
                textViewDate.text = note.date
                switchVisibility(buttonNewNote)
                switchVisibility(textViewDescription)
                switchVisibility(textViewNewNoteTitle)
                switchVisibility(imageViewCalendarEdit)
                switchVisibility(textViewDate)
                for (item in note.listOfTasks) {
                    if (item.isChecked) list.add(item.id)
                }


                calculateProgress()
                imageViewCalendarEdit.setOnClickListener {
                    switchVisibilityForCalendar()
                    calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->



                        textViewDate.text = String.format(
                            getString(R.string.date),
                            dateIntToString(dayOfMonth, 0), dateIntToString(month, 1), year
                        )
                        note.date = textViewDate.text.toString()
                        switchVisibilityForCalendar()
                        updated = true
                        viewModel.updateNote(note)
                    }
                }

                imageViewEditDescription.visibility = View.VISIBLE
                imageViewEditDescription.setOnClickListener {
                    switchVisibility(textViewDescription)
                    switchVisibility(editTextDescription)
                    switchVisibility(buttonNewNote)
                    editTextDescription.setText(note.description)
                }
            }
        } else {
            switchVisibility(binding.buttonAddCheckListItem)
        }

        binding.buttonAddCheckListItem.setOnClickListener {
            with(binding) {
                buttonAddCheckListItem.visibility = View.GONE
                buttonAddItemToCheckList.visibility = View.VISIBLE
                editTextCheckTitle.visibility = View.VISIBLE
                buttonAddItemToCheckList.setOnClickListener {
                    val title = editTextCheckTitle.text.toString()
                    val id = System.currentTimeMillis()
                    if (title != "") {
                        (note.listOfTasks as ArrayList).add(CheckNoteItem(title, false, "$id"))
                        calculateProgress()
                        viewModel.updateNote(note)

                        buttonAddCheckListItem.visibility = View.VISIBLE
                        buttonAddItemToCheckList.visibility = View.GONE
                        editTextCheckTitle.visibility = View.GONE
                    } else
                        Toast.makeText(
                            requireContext(),
                            "Введите название элемента",
                            Toast.LENGTH_SHORT
                        ).show()
                }

            }
        }



        binding.buttonNewNote.setOnClickListener {
            with(binding) {
                val title: String = newNoteTitle.text.toString().trim()
                val description: String = editTextDescription.text.toString().trim()
                if (note.title == "") {
                    if (title.isNotEmpty()) {
                        viewModel.createNewNote(title, description, board, listOfNotesItem, user)
                    }
                } else {
                    note.description = description
                    switchVisibility(buttonNewNote)
                    textViewDescription.text = description.trim()
                    switchVisibility(textViewDescription)
                    switchVisibility(editTextDescription)
                    viewModel.updateNote(note)
                }
            }
        }
        binding.textInList.text =
            String.format(getString(R.string.note_in_list), listOfNotesItem.title)


    }

    private fun dateIntToString(item: Int, predicate: Int): String {
        var newItem = ""
        newItem = if (item + predicate < 10) "0${item + predicate}"
        else "${item + predicate}"
        return newItem
    }

    private fun calculateProgress() {
        with(binding) {
            var size: Double = note.listOfTasks.size.toDouble()
            if (size != 0.0) size = 1 / size
            val progressPercent = (list.size * 100 * size).roundToInt()
            progressBar.progress = progressPercent
            textViewCheckPercent.text =
                String.format(getString(R.string.check_percent), progressPercent, "%")
            progressBar.visibility = View.VISIBLE
            textViewCheckPercent.visibility = View.VISIBLE
        }

    }

    private fun switchVisibilityForCalendar() {
        with(binding) {
            switchVisibility(calendarView)
            switchVisibility(textViewCheckPercent)
            switchVisibility(recyclerViewCheckList)
            switchVisibility(textViewCheckList)
            switchVisibility(imageViewChecklist)
            switchVisibility(buttonAddCheckListItem)
            switchVisibility(progressBar)
        }
    }

    private fun switchVisibility(view: View) {
        if (view.visibility == View.VISIBLE) {
            view.visibility = View.GONE
        } else
            view.visibility = View.VISIBLE
    }

    private fun initViews() {
        recyclerView = binding.recyclerViewCheckList
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        newNoteAdapter = NewNoteCheckItemAdapter()
        newNoteAdapter.checkItemsList = ArrayList(note.listOfTasks)
        recyclerView.adapter = newNoteAdapter
        (recyclerView.adapter as NewNoteCheckItemAdapter).onItemClick = {
            if (list.contains(it.id)) list.remove(it.id)
            else list.add(it.id)
            val index = note.listOfTasks.indexOf(it)
            note.listOfTasks[index].isChecked = !it.isChecked
            calculateProgress()
            viewModel.updateNote(note)

        }
    }

    private fun parseArgs() {
        listOfLists = args.listOfLists
        note = args.note
        board = args.board
        listOfNotesItem = args.listOfNotesItem
        user = args.user

    }

    private fun observeViewModel() {
        viewModel.success.observe(viewLifecycleOwner, Observer {
            launchBoardFragment(it, user)
        })
        viewModel.noteData.observe(viewLifecycleOwner, Observer {
            newNoteAdapter.checkItemsList = it
        })
        viewModel.error.observe(viewLifecycleOwner, Observer {

        })
    }

    private fun launchBoardFragment(board: Board, user: User) {
        findNavController().popBackStack()
    }

    companion object {
        const val KEY_LIST_NOTE = "list"
        const val KEY_BOARD = "board"
        const val KEY_USER = "user"
        const val KEY_NOTE = "note"

        fun newInstance(
            list: ListOfNotesItem,
            board: Board,
            user: User,
            note: Note
        ): NewNoteFragment {
            return NewNoteFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_LIST_NOTE, list)
                    putParcelable(KEY_BOARD, board)
                    putParcelable(KEY_USER, user)
                    putParcelable(KEY_NOTE, note)
                }
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()

        if (note.creatorId == user.id) {
            menuInflater.inflate(R.menu.note_menu, menu)
        }
    }


    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.item_move_note -> {

                ListDialogFragment(listOfLists.toList()).show(
                    childFragmentManager,
                    "ListDialogFragment"
                )
            }
            R.id.item_delete_note -> {
                viewModel.deleteNote(note, board, listOfNotesItem)
                findNavController().popBackStack()
            }
            R.id.item_change_priority -> {
                val array = arrayOf(
                    "High",
                    "Medium",
                    "Low"
                )
                val builder = AlertDialog.Builder(requireActivity())
                builder.setTitle("Выберите приоритет")
                builder.setItems(array, object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        note.priority = when (array[which]) {
                            "High" -> UrgencyOfNote.HIGH
                            "Medium" -> UrgencyOfNote.MIDDLE
                            "Low" -> UrgencyOfNote.LOW
                            else -> UrgencyOfNote.LOW
                        }
                        updated = true
                        viewModel.updateNote(note)
                    }

                })
                builder.create().show()
            }
            android.R.id.home -> findNavController().popBackStack()
        }
        return true
    }

    fun moveNote(listItem: ListOfNotesItem) {
        viewModel.moveNote(note, listItem, board, user)
    }
}