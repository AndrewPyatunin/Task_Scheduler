package com.example.taskscheduler.presentation.boardupdated

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
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
import com.example.taskscheduler.presentation.ViewModelFactory
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import javax.inject.Inject

class OuterBoardFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var tabLayout: TabLayout
    private lateinit var binding: FragmentOuterBoardBinding
    private lateinit var board: Board
    private lateinit var user: User
    private var parentAdapter: OuterBoardAdapter? = null
    private var parentList = emptyList<NotesListItem>()
    private var viewPager: ViewPager2? = null
    private var currentPosition = 0

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[OuterBoardViewModel::class.java]
    }

    private val args by navArgs<OuterBoardFragmentArgs>()

    companion object {

        const val KEY_REQUEST_DIALOG = "request_dialog"
        const val KEY_BUNDLE_DIALOG = "bundle_dialog"
        const val PAGER_POSITION = "position"
        const val DIALOG_FRAGMENT_TAG = "CreateNewListDialogFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArgs()
        childFragmentManager.setFragmentResultListener(KEY_REQUEST_DIALOG, this) { _, bundle ->
            val listTitle = bundle.getString(KEY_BUNDLE_DIALOG) ?: "title"
            viewModel.createNewList(listTitle, board, user)
        }
        MyDatabaseConnection.updated = true
    }

    override fun onPause() {
        super.onPause()
        val item = viewPager?.currentItem ?: 0
        requireArguments().putInt(PAGER_POSITION, item)
        parentList = emptyList()
        MyDatabaseConnection.currentPosition = item
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOuterBoardBinding.inflate(inflater, container, false)
        registerForContextMenu(binding.viewPager)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentPosition = MyDatabaseConnection.currentPosition
        viewModel.getBoard(board)
        observeViewModel()
        binding.imageViewInvite.setOnClickListener {
            launchInviteUserFragment()
        }
        val textView = binding.textViewAddList
        textView.setOnClickListener {
            CreateNewListDialogFragment().show(childFragmentManager, DIALOG_FRAGMENT_TAG)
        }
        requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    retryToListBoard()
                }
            })
        savedInstanceState?.putInt(PAGER_POSITION, viewPager?.currentItem ?: 0)
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

    private fun retryToListBoard() {
        findTopNavController().popBackStack(R.id.boardListFragment, false)
    }

    private fun initViewPager(list: List<NotesListItem>) {
        if (list != parentList || list.isEmpty()) {
            parentAdapter =
                OuterBoardAdapter(
                    lifecycle =  lifecycle,
                    fragmentManager =  childFragmentManager,
                    list = list as ArrayList<NotesListItem>,
                    board =  board,
                    user = user,
                )
            parentList = list
            if (list.isNotEmpty()) {
                binding.tabLayout.visibility = View.VISIBLE
            } else {
                binding.tabLayout.visibility = View.GONE
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
            viewPager?.setCurrentItem(MyDatabaseConnection.currentPosition, false)
            tabLayout.getTabAt(MyDatabaseConnection.currentPosition)?.select()
        }
    }

    private fun observeViewModel() {

        viewModel.listReady.observe(viewLifecycleOwner) {
            viewModel.readData(board)
        }

        viewModel.listLiveData.observe(viewLifecycleOwner) { list ->
            binding.loadingIndicatorBoard.visibility = View.GONE
            initViewPager(list)
        }
        viewModel.boardLiveData.observe(viewLifecycleOwner) {
            board = it
            viewModel.fetchNotesLists(board, parentList)
        }
    }

    private fun fillingLayout(draw: Drawable?) {
        with(binding) {
            constraint.background = draw
            cardViewBoard.visibility = View.VISIBLE
            imageViewInvite.visibility = View.VISIBLE
            viewPager.visibility = View.VISIBLE
        }
    }
}