package com.example.taskscheduler.presentation.board

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.taskscheduler.ParentListNoteRVAdapter
import com.example.taskscheduler.databinding.FragmentBoardBinding
import com.example.taskscheduler.domain.*

class BoardFragment : Fragment() {

    lateinit var binding: FragmentBoardBinding
//    private val binding: FragmentBoardBinding
//        get() = _binding ?: throw RuntimeException("FragmentBoardBinding==null")
    lateinit var board: Board
    lateinit var user: User
    private lateinit var recyclerViewParent: RecyclerView
    lateinit var parentAdapter: ParentListNoteRVAdapter
    lateinit var viewModel: BoardViewModel
    private var parentList = ArrayList<ListOfNotesItem>()

    private val args by navArgs<BoardFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = parseArgs()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("USER_onVIEW", user.name)
        viewModel = ViewModelProvider(this)[BoardViewModel::class.java]
        initViews()
        observeViewModel()
        binding.imageViewInvite.setOnClickListener {
            launchInviteUserFragment()
        }
        binding.buttonInvite.setOnClickListener {

        }
        Glide.with(this).load(board.backgroundUrl).into(object : CustomTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                binding.scrollView.background = resource
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                binding.scrollView.background = placeholder
            }

        })
        val textView = binding.textViewAddList
        val editText = binding.editTextAddList

        val button = binding.buttonAddNewList
        textView.setOnClickListener {
            textView.visibility = View.GONE
            editText.visibility = View.VISIBLE
            button.visibility = View.VISIBLE
//            parentList.add(ListOfNotesItem(editText.text.toString(), ArrayList()))
//            parentAdapter.parentListFrom = parentList
        }
        button.setOnClickListener {
            val textTitle = editText.text.toString().trim()
            if (textTitle.isNotEmpty()) {
                //parentList.add(ListOfNotesItem("5", textTitle, ArrayList()))
                viewModel.createNewList(textTitle, board)
                parentAdapter.parentListFrom = parentList
                parentAdapter.notifyDataSetChanged()
                textView.visibility = View.VISIBLE
                editText.visibility = View.GONE
                button.visibility = View.GONE
            } else
                Toast.makeText(requireContext(), "Заполните поле ввода", Toast.LENGTH_SHORT).show()
        }
        requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                retryToListBoard()
            }

        })
    }



//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }

    private fun initViews() {
        recyclerViewParent = binding.parentRecyclerViewBoard
        recyclerViewParent.setHasFixedSize(true)
        recyclerViewParent.layoutManager = LinearLayoutManager(requireContext())
        viewModel.readData(board.id)
//        fillingList()

        parentAdapter = ParentListNoteRVAdapter(parentList, object : MyNoteClickHandler {
            override fun onNoteClicked(note: Note) {
                Toast.makeText(requireContext(), note.title, Toast.LENGTH_SHORT).show()
            }

            override fun onTextClicked(resId: Int, position: Int) {
                Log.i("USER_PARENT_LIST",parentList[position].title)
                launchNewNoteFragment(parentList[position])
            }

        })

        //parentAdapter.parentList = parentList
        recyclerViewParent.adapter = parentAdapter
//        parentAdapter.childNoteRVAdapter.onItemClick = {
//            Toast.makeText(requireContext(), it.title, Toast.LENGTH_SHORT).show()
//        }


    }

//    fun fillingList() {
//        val first = Note("1","Quick", emptyList(), "description", "labels", emptyList())
//        val second = Note("2","Fast", emptyList(), "description", "labels", emptyList())
//        val third = Note("3","Low", emptyList(), "description", "labels", emptyList())
//        val fourth = Note("4", "Slow", emptyList(), "description", "labels", emptyList())
//        val fifth = Note("5", "Go", emptyList(), "description", "labels", emptyList())
//        parentList.add(ListOfNotesItem("0", "Need to do", arrayListOf(Note("0","Go to party", emptyList(), "description", "labels", emptyList()), first, second, third, fourth, fifth)))
//        parentList.add(ListOfNotesItem("1","Done", arrayListOf(Note("7", "Work", emptyList(), "description", "labels", emptyList()))))
//        parentList.add(ListOfNotesItem("2", "In process", arrayListOf(Note("8", "Writing diplomacy", emptyList(), "description", "labels", emptyList()))))
////        Log.i("USER_LIST_OF_NOTES", parentList[1].listNotes[1].toString())
//    }

    private fun parseArgs(): User {
        board = args.board
        user = args.user
//        requireArguments().getParcelable<Board>(KEY_BOARD)?.let {
//            board = it
//        }
//        requireArguments().getParcelable<User>(KEY_USER)?.let {
//            Log.i("USER_FROM_NEW", it.name)
//            user = it
//        }
        return user
    }

    private fun launchInviteUserFragment() {
        findNavController().navigate(BoardFragmentDirections.actionBoardFragmentToInviteUserFragment(board, user))
//        requireActivity().supportFragmentManager.beginTransaction()
//            .replace(R.id.fragment_container, InviteUserFragment.newInstance(board, user))
//            .addToBackStack(null)
//            .commit()
    }

    fun launchNewNoteFragment(listOfNotesItem: ListOfNotesItem) {
        findNavController().navigate(
            BoardFragmentDirections.actionBoardFragmentToNewNoteFragment(listOfNotesItem, board, user))
//        requireActivity().supportFragmentManager.beginTransaction()
//            .addToBackStack(null)
//            .replace(R.id.fragment_container, NewNoteFragment.newInstance(listOfNotesItem, board, user))
//            .commit()
    }

    fun retryToListBoard() {
        findNavController().popBackStack()
//        findNavController().navigate(BoardFragmentDirections.actionBoardFragmentToBoardListFragment(user, ListOfBoards(ArrayList())))
//        requireActivity().supportFragmentManager.popBackStack(BoardListFragment.NAME_BOARD_LIST, 0)
//        Log.i("USER_BOARD_FRAG", user.name)
//        requireActivity().supportFragmentManager.beginTransaction()
//            .replace(R.id.fragment_container, BoardListFragment.newInstance(user, ArrayList()))
//            .commit()
    }

    private fun observeViewModel() {
        viewModel.listLiveData.observe(viewLifecycleOwner, Observer {
            parentList = it as ArrayList<ListOfNotesItem>
//            Log.i("USER_OBSERVE_LIST", parentList[1].title)
            parentAdapter.parentList = parentList

            recyclerViewParent.adapter = parentAdapter
            parentAdapter.notifyDataSetChanged()
        })
        viewModel.boardLiveData.observe(viewLifecycleOwner, Observer {
            board = it
        })
    }

    companion object {

        private const val KEY_BOARD = "board"
        private const val KEY_USER = "user"

        fun newInstance(board: Board, user: User): BoardFragment {
            return BoardFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_BOARD, board)
                    putParcelable(KEY_USER, user)
                }
            }
        }
    }

    interface MyNoteClickHandler {
        fun onNoteClicked(note: Note)
        fun onTextClicked(resId: Int, position: Int)
    }
}