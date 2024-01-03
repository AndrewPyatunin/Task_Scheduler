package com.example.taskscheduler.presentation.newnote

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskscheduler.MyDatabaseConnection.updated
import com.example.taskscheduler.R
import com.example.taskscheduler.databinding.FragmentNewNoteBinding
import com.example.taskscheduler.domain.CheckNoteItem
import com.example.taskscheduler.domain.UrgencyOfNote
import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.Note
import com.example.taskscheduler.domain.models.NotesListItem
import com.example.taskscheduler.domain.models.User
import kotlin.math.roundToInt

class NewNoteFragment : Fragment(), MenuProvider {

    lateinit var binding: FragmentNewNoteBinding
    lateinit var notesListItem: NotesListItem
    lateinit var board: Board
    lateinit var user: User
    lateinit var note: Note
    lateinit var newNoteAdapter: NewNoteCheckItemAdapter
    lateinit var recyclerView: RecyclerView
    lateinit var listOfLists: Array<NotesListItem>
    var list = ArrayList<String>()
    private val viewModel by lazy {
        ViewModelProvider(this)[NewNoteViewModel::class.java]
    }

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
        updated = false
        observeViewModel()
        binding.cardNewNoteDescription.background.alpha = 0
        binding.newNoteCard.background.alpha = 0
        initRecyclerView()
        if (note.id != "") {
            initViews()
        } else {
            switchVisibility(binding.buttonAddCheckListItem)
        }

        binding.buttonAddCheckListItem.setOnClickListener {
            checkListItemClick()
        }

        binding.buttonNewNote.setOnClickListener {
            with(binding) {
                val title: String = newNoteTitle.text.toString().trim()
                val description: String = editTextDescription.text.toString().trim()
                if (note.title == "") {
                    if (title.isNotEmpty()) {
                        viewModel.createNewNote(title, description, board, notesListItem, user)
                    }
                } else {
                    switchVisibility(buttonNewNote)
                    textViewDescription.text = description.trim()
                    switchVisibility(textViewDescription)
                    switchVisibility(editTextDescription)
                    viewModel.updateNote(note.copy(description = description))
                }
            }
        }
        binding.textInList.text =
            String.format(getString(R.string.note_in_list), notesListItem.title)

    }

    private fun checkListItemClick() {
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
                        getString(R.string.select_check_list_title),
                        Toast.LENGTH_SHORT
                    ).show()
            }
        }
    }

    private fun initViews() {
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
            note.listOfTasks.forEach {
                if (it.isChecked) list.add(it.id)
            }

            calculateProgress()
            imageViewCalendarEdit.setOnClickListener {
                switchVisibilityForCalendar()
                calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
                    textViewDate.text = String.format(
                        getString(R.string.date),
                        dateIntToString(dayOfMonth, 0), dateIntToString(month, 1), year
                    )
                    switchVisibilityForCalendar()
                    updated = true
                    viewModel.updateNote(note.copy(date = textViewDate.text.toString()))
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
    }

    private fun dateIntToString(item: Int, predicate: Int): String {
        return if (item + predicate < 10) "0${item + predicate}"
        else "${item + predicate}"
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

    private fun initRecyclerView() {
        recyclerView = binding.recyclerViewCheckList
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        newNoteAdapter = NewNoteCheckItemAdapter()
        newNoteAdapter.checkItemsList = ArrayList(note.listOfTasks)
        recyclerView.adapter = newNoteAdapter
        (recyclerView.adapter as NewNoteCheckItemAdapter).onItemClick = {
            if (list.contains(it.id)) list.remove(it.id)
            else list.add(it.id)
            calculateProgress()
            viewModel.updateNote(note.copy(listOfTasks = note.listOfTasks.map { item ->
                if (item == it) {
                    item.copy(isChecked = !it.isChecked)
                } else {
                    item
                }
            }))

        }
    }

    private fun parseArgs() {
        listOfLists = args.listOfLists
        note = args.note
        board = args.board
        notesListItem = args.listOfNotesItem
        user = args.user

    }

    private fun observeViewModel() {
        viewModel.success.observe(viewLifecycleOwner) {
            launchBoardFragment(it, user)
        }
        viewModel.noteData.observe(viewLifecycleOwner) {
            newNoteAdapter.checkItemsList = it
        }
        viewModel.error.observe(viewLifecycleOwner) {

        }
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
            list: NotesListItem,
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
                viewModel.deleteNote(note, board, notesListItem)
                findNavController().popBackStack()
            }
            R.id.item_change_priority -> {
                val array = arrayOf(
                    "High",
                    "Medium",
                    "Low"
                )
                val builder = AlertDialog.Builder(requireActivity())
                builder.setTitle(getString(R.string.choose_note_priority))
                builder.setItems(
                    array
                ) { dialog, which ->
                    val priority = when (array[which]) {
                        "High" -> UrgencyOfNote.HIGH
                        "Medium" -> UrgencyOfNote.MIDDLE
                        "Low" -> UrgencyOfNote.LOW
                        else -> UrgencyOfNote.LOW
                    }
                    updated = true
                    viewModel.updateNote(note.copy(priority = priority))
                }
                builder.create().show()
            }
            android.R.id.home -> findNavController().popBackStack()
        }
        return true
    }

    fun moveNote(listItem: NotesListItem) {
        viewModel.moveNote(note, listItem, board, user)
    }
}