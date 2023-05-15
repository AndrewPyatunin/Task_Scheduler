package com.example.taskscheduler.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.taskscheduler.NewNoteViewModel
import com.example.taskscheduler.R
import com.example.taskscheduler.databinding.FragmentNewNoteBinding
import com.example.taskscheduler.domain.Board
import com.example.taskscheduler.domain.ListOfNotesItem
import com.example.taskscheduler.domain.User

class NewNoteFragment : Fragment() {
    lateinit var binding: FragmentNewNoteBinding
    lateinit var listOfNotesItem: ListOfNotesItem
    lateinit var board: Board
    lateinit var user: User
    lateinit var viewModel: NewNoteViewModel

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
        binding.textInList.text = String.format(getString(R.string.note_in_list), listOfNotesItem.title)
        binding.buttonNewNote.setOnClickListener {
            val title = binding.newNoteTitle.text.toString().trim()
            val description = binding.editTextDescription.text.toString().trim()
            if (title.isNotEmpty()) {
                viewModel.createNewNote(title, description, board, listOfNotesItem)
            }
        }


    }

    private fun parseArgs() {
//        board = requireArguments().getParcelable<Board>(KEY_BOARD) ?: Board()
//        listOfNotesItem = requireArguments().getParcelable<ListOfNotesItem>(KEY_LIST_NOTE) ?: ListOfNotesItem()
//        user = requireArguments().getParcelable<User>(KEY_USER) ?: User()
        board = args.board
        listOfNotesItem = args.list
        user = args.user
    }
    private fun observeViewModel() {
//        viewModel.boardLiveData.observe(viewLifecycleOwner, Observer {
//            board = it
//
//        })
        viewModel.noteData.observe(viewLifecycleOwner, Observer {
            launchBoardFragment(board, user)
        })
        viewModel.error.observe(viewLifecycleOwner, Observer {

        })
    }

    private fun launchBoardFragment(board: Board, user: User) {
        findNavController().navigate(NewNoteFragmentDirections.actionNewNoteFragmentToBoardFragment(board, user))
//        requireActivity().supportFragmentManager.beginTransaction()
//            .addToBackStack(null)
//            .replace(R.id.fragment_container, BoardFragment.newInstance(board, user))
//            .commit()
    }

    companion object {
        const val KEY_LIST_NOTE = "list"
        const val KEY_BOARD = "board"
        const val KEY_USER = "user"

        fun newInstance(list: ListOfNotesItem, board: Board, user: User): NewNoteFragment {
            return NewNoteFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_LIST_NOTE, list)
                    putParcelable(KEY_BOARD, board)
                    putParcelable(KEY_USER, user)
                }
            }
        }
    }
}