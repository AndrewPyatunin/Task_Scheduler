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

//    private val args by navArgs<BoardFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        user = parseArgs()
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
//        binding.imageViewInvite.setOnClickListener {
//            launchInviteUserFragment()
//        }
        binding.buttonInvite.setOnClickListener {

        }
//        Glide.with(this).load(board.backgroundUrl).into(object : CustomTarget<Drawable>() {
//            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
//                binding.scrollView.background = resource
//
//            }
//
//            override fun onLoadCleared(placeholder: Drawable?) {
//                binding.scrollView.background = placeholder
//            }
//
//        })
        val textView = binding.textViewAddList
        val editText = binding.editTextAddList

        val buttonAddNewList = binding.buttonAddNewList
        textView.setOnClickListener {
            binding.cardViewBoard.visibility = View.VISIBLE
            textView.visibility = View.GONE
            editText.visibility = View.VISIBLE
            buttonAddNewList.visibility = View.VISIBLE
//            parentList.add(ListOfNotesItem(editText.text.toString(), ArrayList()))
//            parentAdapter.parentListFrom = parentList
        }
        buttonAddNewList.setOnClickListener {

            val textTitle = editText.text.toString().trim()
            if (textTitle.isNotEmpty()) {
                val list = ArrayList(parentList)
                //parentList.add(ListOfNotesItem("5", textTitle, ArrayList()))
                viewModel.createNewList(textTitle, board)
                parentAdapter.parentListFrom = list
//                parentAdapter.notifyDataSetChanged()
                textView.visibility = View.VISIBLE
                editText.visibility = View.GONE
                buttonAddNewList.visibility = View.GONE
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


    private fun initViews() {
        recyclerViewParent = binding.parentRecyclerViewBoard
        recyclerViewParent.setHasFixedSize(true)
        recyclerViewParent.layoutManager = LinearLayoutManager(requireContext())
        viewModel.readData(board.id)
//        fillingList()
        val list = ArrayList(parentList)

//        parentAdapter = ParentListNoteRVAdapter(list, object : MyNoteClickHandler {
////            override fun onNoteClicked(note: Note, listOfNotesItem: ListOfNotesItem) {
////                launchNewNoteFragment(listOfNotesItem, note)
////                Toast.makeText(requireContext(), note.title, Toast.LENGTH_SHORT).show()
////            }
////
////            override fun onTextClicked(resId: Int, position: Int) {
//////                Log.i("USER_PARENT_LIST",parentList[position].title)
////                launchNewNoteFragment(parentList[position], Note())
////            }
//
//        })

        recyclerViewParent.adapter = parentAdapter

    }


//    private fun parseArgs(): User {
//        board = args.board
//        user = args.user
//        return user
//    }
//
//    private fun launchInviteUserFragment() {
//        findNavController().navigate(BoardFragmentDirections.actionBoardFragmentToInviteUserFragment(board, user))
//    }
//
//    fun launchNewNoteFragment(listOfNotesItem: ListOfNotesItem, note: Note) {
//        findNavController().navigate(
//            BoardFragmentDirections.actionBoardFragmentToNewNoteFragment(listOfNotesItem, board, user, note))
//    }

    fun retryToListBoard() {
        findNavController().popBackStack()
    }

    private fun observeViewModel() {
        viewModel.listLiveData.observe(viewLifecycleOwner, Observer {
            parentList = it as ArrayList<ListOfNotesItem>
//            Log.i("USER_OBSERVE_LIST", parentList[1].title)
            val list = ArrayList(it)
            parentAdapter.parentList = list

            recyclerViewParent.adapter = parentAdapter
//            parentAdapter.notifyDataSetChanged()
            Glide.with(this).load(board.backgroundUrl).into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    with(binding) {
                        scrollView.background = resource
                        loadingIndicatorBoard.visibility = View.GONE
                        pleaseWaitTextViewBoard.visibility = View.GONE
                        cardViewBoard.visibility = View.VISIBLE
                        imageViewInvite.visibility = View.VISIBLE
                        recyclerViewParent.visibility = View.VISIBLE
                    }

                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    binding.scrollView.background = placeholder
                }

            })

//            recyclerViewParent.visibility = View.VISIBLE


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
        fun onNoteClicked(note: Note, listOfNotesItem: ListOfNotesItem)
        fun onTextClicked(resId: Int, position: Int)
    }
}