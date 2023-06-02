package com.example.taskscheduler.presentation.newnote

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskscheduler.R
import com.example.taskscheduler.databinding.FragmentNewNoteBinding
import com.example.taskscheduler.domain.*
import com.example.taskscheduler.findTopNavController
import com.example.taskscheduler.presentation.boardupdated.InnerBoardFragment
import kotlin.math.roundToInt

class NewNoteFragment : Fragment() {
    lateinit var binding: FragmentNewNoteBinding
    lateinit var listOfNotesItem: ListOfNotesItem
    lateinit var board: Board
    lateinit var user: User
    lateinit var note: Note
    lateinit var viewModel: NewNoteViewModel
    lateinit var newNoteAdapter: NewNoteCheckItemAdapter
    lateinit var recyclerView: RecyclerView
    var list = ArrayList<String>()

    private val args by navArgs<NewNoteFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewNoteBinding.inflate(inflater, container, false)
        return binding.root//super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArgs()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[NewNoteViewModel::class.java]
        observeViewModel()
        binding.cardNewNoteDescription.background.alpha = 0
        binding.newNoteCard.background.alpha = 0
        initViews()
        if (note.id != "") {
            with(binding) {
                newNoteTitle.visibility = View.GONE
                editTextDescription.visibility = View.GONE
                textViewDescription.text = note.description
                textViewNewNoteTitle.text = note.title
                switchVisibility(buttonNewNote)
                switchVisibility(textViewDescription)
                switchVisibility(textViewNewNoteTitle)
                for (item in note.listOfTasks) {
                    if(item.isChecked) list.add(item.id)
                }
                calculateProgress()

//                buttonNewNote.visibility = View.GONE
//                textViewDescription.visibility = View.VISIBLE
//                textViewNewNoteTitle.visibility = View.VISIBLE
                imageViewEditDescription.visibility = View.VISIBLE
                imageViewEditDescription.setOnClickListener {
                    switchVisibility(textViewCheckPercent)
//                    switchVisibility(progressBar)
                    switchVisibility(textViewDescription)
                    switchVisibility(editTextDescription)
                    switchVisibility(buttonNewNote)
                    editTextDescription.setText(note.description)
                }
            }
        } else {
//            switchVisibility(binding.imageViewDescription)
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
                        viewModel.updateNote(note, board, listOfNotesItem)
//                        newNoteAdapter.checkItemsList = note.listOfTasks
//                        newNoteAdapter.notifyDataSetChanged()
                        buttonAddCheckListItem.visibility = View.VISIBLE
                        buttonAddItemToCheckList.visibility = View.GONE
                        editTextCheckTitle.visibility = View.GONE
                    } else
                        Toast.makeText(requireContext(), "Введите название элемента", Toast.LENGTH_SHORT).show()
                }

            }
        }



        binding.buttonNewNote.setOnClickListener {
            val title: String = binding.newNoteTitle.text.toString().trim()
            val description: String = binding.editTextDescription.text.toString().trim()
            if (note.title == "") {
                if (title.isNotEmpty()) {
                    viewModel.createNewNote(title, description, board, listOfNotesItem)
                }
            } else {
                note.description = description
                viewModel.updateNote(note, board, listOfNotesItem)
            }
        }
        binding.textInList.text = String.format(getString(R.string.note_in_list), listOfNotesItem.title)


    }

    private fun calculateProgress() {
        with(binding) {
            var size: Double = note.listOfTasks.size.toDouble()
            if (size != 0.0) size = 1/size
            val progressPercent = (list.size * 100 * size).roundToInt()
            progressBar.progress = progressPercent
            textViewCheckPercent.text = String.format(getString(R.string.check_percent), progressPercent, "%")
            progressBar.visibility = View.VISIBLE
            textViewCheckPercent.visibility = View.VISIBLE
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
//        newNoteAdapter.onItemClick = {
//            it.isChecked = !it.isChecked
////            if (list.contains(it.id)) {
////                it.isChecked = false
////                list.remove(it.id)
////            }
////            else {
////                it.isChecked = true
////                list.add(it.id)
////            }
//            note.listOfTasks
//            for (item in  note.listOfTasks) {
//                item.isChecked
//            }
//            note.listOfTasks
//            viewModel.updateNote(note, board, listOfNotesItem)
//        }
        recyclerView.adapter = newNoteAdapter
        (recyclerView.adapter as NewNoteCheckItemAdapter).onItemClick = {
//            it.isChecked = !it.isChecked
            if (list.contains(it.id)) list.remove(it.id)
            else list.add(it.id)
            val index = note.listOfTasks.indexOf(it)
            note.listOfTasks[index].isChecked = !it.isChecked
            calculateProgress()
            viewModel.updateNote(note, board, listOfNotesItem)

        }
    }
    private fun parseArgs() {
//        board = requireArguments().getParcelable<Board>(KEY_BOARD) ?: Board()
//        listOfNotesItem = requireArguments().getParcelable<ListOfNotesItem>(KEY_LIST_NOTE) ?: ListOfNotesItem()
//        user = requireArguments().getParcelable<User>(KEY_USER) ?: User()
//        note = requireArguments().getParcelable(KEY_NOTE) ?: Note()
        note = args.note
        board = args.board
        listOfNotesItem = args.listOfNotesItem
        user = args.user

    }
    private fun observeViewModel() {
//        viewModel.boardLiveData.observe(viewLifecycleOwner, Observer {
//            board = it
//
//        })
        viewModel.success.observe(viewLifecycleOwner, Observer {
            launchBoardFragment(it, user)
        })
        viewModel.noteData.observe(viewLifecycleOwner, Observer {
            newNoteAdapter.checkItemsList = it
//            newNoteAdapter = NewNoteCheckItemAdapter(it)
        })
        viewModel.error.observe(viewLifecycleOwner, Observer {

        })
    }

    private fun launchBoardFragment(board: Board, user: User) {
//        requireActivity().supportFragmentManager.beginTransaction()
//            .addToBackStack(null)
//            .replace(R.id.fragment_container, InnerBoardFragment.newInstance(listOfNotesItem))
//            .commit()

//        findNavController().navigate(NewNoteFragmentDirections.actionNewNoteFragmentToInnerBoardFragment(listOfNotesItem))
        findNavController().navigate(NewNoteFragmentDirections.actionNewNoteFragmentToOuterBoardFragment(user, board))
//        findNavController().navigate(NewNoteFragmentDirections.actionNewNoteFragmentToBoardFragment(board, user))
    }

    companion object {
        const val KEY_LIST_NOTE = "list"
        const val KEY_BOARD = "board"
        const val KEY_USER = "user"
        const val KEY_NOTE = "note"

        fun newInstance(list: ListOfNotesItem, board: Board, user: User, note: Note): NewNoteFragment {
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
}