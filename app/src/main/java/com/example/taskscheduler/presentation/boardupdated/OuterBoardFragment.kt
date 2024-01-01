package com.example.taskscheduler.presentation.boardupdated

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
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.taskscheduler.MyDatabaseConnection
import com.example.taskscheduler.R
import com.example.taskscheduler.databinding.FragmentOuterBoardBinding
import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.NotesListItem
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.findTopNavController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class OuterBoardFragment : Fragment() {

    lateinit var binding: FragmentOuterBoardBinding
    lateinit var board: Board
    lateinit var user: User
    private var parentAdapter: OuterBoardAdapter? = null
    lateinit var viewModel: OuterBoardViewModel
    private var parentList = emptyList<NotesListItem>()
    private var viewPager: ViewPager2? = null
    private lateinit var tabLayout: TabLayout
    var currentPosition = 0
    var isCreated = false

    private val args by navArgs<OuterBoardFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArgs()
        parentList = MyDatabaseConnection.parentList
        MyDatabaseConnection.currentPosition = 0
        MyDatabaseConnection.updated = true
    }

    override fun onPause() {
        super.onPause()
//        MyDatabaseConnection.parentList = parentList
        val item = viewPager?.currentItem ?: 0
        requireArguments().putInt("position", item)
        Log.i("USER_SAVED", item.toString())
        MyDatabaseConnection.currentPosition = item
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOuterBoardBinding.inflate(inflater, container, false)
        registerForContextMenu(binding.viewPager)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[OuterBoardViewModel::class.java]

        if (MyDatabaseConnection.updated) {
            viewModel.fetchNotesLists(board, parentList)
        }


        currentPosition = MyDatabaseConnection.currentPosition
        observeViewModel()
        binding.imageViewInvite.setOnClickListener {
            launchInviteUserFragment()
        }
        binding.buttonInvite.setOnClickListener {

        }
        val textView = binding.textViewAddList
        val editText = binding.editTextAddList

        val buttonAddNewList = binding.buttonAddNewList
        textView.setOnClickListener {
            binding.cardViewBoard.visibility = View.VISIBLE
            textView.visibility = View.GONE
            editText.visibility = View.VISIBLE
            buttonAddNewList.visibility = View.VISIBLE
        }
//        initViewPager(parentList as? ArrayList ?: ArrayList())
        buttonAddNewList.setOnClickListener {

            val textTitle = editText.text.toString().trim()
            if (textTitle.isNotEmpty()) {
                val item = viewModel.createNewList(textTitle, board, user)
                textView.visibility = View.VISIBLE
                editText.visibility = View.INVISIBLE
                buttonAddNewList.visibility = View.INVISIBLE
            } else
                Toast.makeText(requireContext(), "Заполните поле ввода", Toast.LENGTH_SHORT).show()
        }
        requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    retryToListBoard()
                }

            })
        savedInstanceState?.putInt("position", viewPager?.currentItem ?: 0)
        savedInstanceState?.putBoolean("first_init", false)
    }


    private fun parseArgs() {
        board = args.board
        user = args.user
    }

    private fun launchInviteUserFragment() {
        findNavController().navigate(
            OuterBoardFragmentDirections.actionOuterBoardFragmentToInviteUserFragment(
                board,
                user
            )
        )
    }

    fun retryToListBoard() {
        findTopNavController().popBackStack(R.id.boardListFragment, false)
    }

    private fun initViewPager(list: List<NotesListItem>) {
        Log.i("OUTER_BOARD_LIST", list.toString())
        parentAdapter =
            OuterBoardAdapter(
            lifecycle,
            childFragmentManager,
            board,
            user,
            list as ArrayList<NotesListItem>
        )
        parentList = list //
        repeat(list.size) {
            binding.tabLayout.visibility = View.VISIBLE
        }
        viewPager = binding.viewPager
        Glide.with(this).load(board.backgroundUrl).into(object : CustomTarget<Drawable>() {
            override fun onResourceReady(
                resource: Drawable,
                transition: Transition<in Drawable>?
            ) {
                fillingLayout(resource)
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                fillingLayout(placeholder)
            }
        })
        viewPager?.offscreenPageLimit = list.size + 1
        tabLayout = binding.tabLayout
        viewPager?.adapter = parentAdapter
        viewPager?.let {
            TabLayoutMediator(tabLayout, it) { tab, position ->
                tab.text = list[position].title
            }.attach()
        }
        tabLayout.getTabAt(currentPosition)?.select()
    }

    private fun observeViewModel() {

        viewModel.listReady.observe(viewLifecycleOwner) {
            Log.i("OUTER_BOARD", board.title)
            viewModel.readData(board)
        }

        viewModel.listLiveData.observe(viewLifecycleOwner) { list ->
            initViewPager(list)
//            parentList = list as? ArrayList<NotesListItem> ?: ArrayList()
//            repeat(list.size) {
//                binding.tabLayout.visibility = View.VISIBLE
//            }
//            parentAdapter = OuterBoardAdapter(
//                lifecycle,
//                childFragmentManager,
//                board,
//                user,
//                parentList as ArrayList
//            )
//            viewPager?.adapter = parentAdapter
//            viewPager?.let {
//                TabLayoutMediator(tabLayout, viewPager!!) { tab, position ->
//                    tab.text = list[position].title
//                }.attach()
//            }
//            tabLayout.getTabAt(currentPosition)?.select()
        }
        viewModel.boardLiveData.observe(viewLifecycleOwner) {
            board = it
        }
    }

    private fun fillingLayout(draw: Drawable?) {
        with(binding) {
            constraint.background = draw
            loadingIndicatorBoard.visibility = View.GONE
            cardViewBoard.visibility = View.VISIBLE
            imageViewInvite.visibility = View.VISIBLE
            viewPager.visibility = View.VISIBLE
        }
    }
}