package com.example.taskscheduler.presentation.boardlist

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.taskscheduler.*
import com.example.taskscheduler.databinding.FragmentBoardListBinding
import com.example.taskscheduler.domain.Board
import com.example.taskscheduler.domain.User
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class BoardListFragment: Fragment(), MenuProvider {
    private var _binding: FragmentBoardListBinding? = null
    private val binding: FragmentBoardListBinding
        get() = _binding ?: throw RuntimeException("FragmentBoardListBinding==null")
    private lateinit var userId: String
    lateinit var user: User
    var boardList = ArrayList<Board>()
    private lateinit var viewModel: BoardListViewModel
    private lateinit var viewModelFactory: BoardListViewModelFactory
    lateinit var recyclerViewBoardList: RecyclerView
    lateinit var boardsAdapter: BoardListAdapter
    private val databaseImageUrlsReference = Firebase.database.getReference("ImageUrls")
//    private val args by navArgs<BoardListFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        parseArgs()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBoardListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModelFactory = BoardListViewModelFactory(User())
        viewModel = ViewModelProvider(this, viewModelFactory)[BoardListViewModel::class.java]
        observeViewModel()
        initViews()
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
//        user = viewModel.user.value ?: User()
//        Log.i("USER_URL_LIST", user.uri)

//        Glide.with(this).load(user.uri).centerCrop().into(binding.imageViewUserAvatarBoardList)
//        binding.buttonInvites.setOnClickListener {
////            Log.i("USER_TO_INVITE", user.name)
//            launchMyInvitesFragment(user)
//        }

        binding.imageViewAddNewBoard.setOnClickListener {
            launchNewBoardFragment(user)
//            Log.i("USER_BOARD_LIST", viewModel.user.value.toString())
//            viewModel.user.value?.let { it1 -> launchNewBoardFragment(it1) }
        }
        boardsAdapter.onItemClick = {
            launchBoardFragment(it)
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModel() {
        viewModel.firebaseUser.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                launchLoginFragment()
            }
        })
        viewModel.boardList.observe(viewLifecycleOwner, Observer {
            boardsAdapter.boards = it
            with(binding) {
                loadingIndicator.visibility = View.GONE
                pleaseWaitTextView.visibility = View.GONE
                recyclerViewBoardList.visibility = View.VISIBLE
            }
        })
        viewModel.user.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                user = it
                Glide.with(this).load(it.uri).centerCrop().into(binding.imageViewUserAvatarBoardList)
                with(binding.textViewWelcomeUser) {
                    text =
                        String.format(getString(R.string.welcome_user, user.name, user.lastName))
                    visibility = View.VISIBLE
                }
            }
        })
    }

    private fun initViews() {
        recyclerViewBoardList = binding.recyclerViewBoardList
        recyclerViewBoardList.layoutManager = GridLayoutManager(requireContext(), 2)
//        recyclerViewBoardList.isNestedScrollingEnabled = false
        boardsAdapter = BoardListAdapter()
        if (boardList.isNotEmpty()) boardsAdapter.boards = boardList
        recyclerViewBoardList.adapter = boardsAdapter
    }

    private fun launchMyInvitesFragment(user: User) {
    }

    private fun launchLoginFragment() {
//        findNavController().navigate(.actionBoardListFragmentToLoginFragment())
    }

    private fun launchNewBoardFragment(user: User) {
        findNavController().navigate(BoardListFragmentDirections.actionBoardListFragmentToNewBoardFragment(user, Board()))
    }

    private fun launchBoardFragment(board: Board) {
        findNavController().navigate(BoardListFragmentDirections.actionBoardListFragmentToOuterBoardFragment(user, board))
    }



    companion object {

        const val NAME_BOARD_LIST = "BoardListFragment"

        private const val KEY_USER = "user"
        private const val KEY_BOARDS = "boards"

        fun newInstance(user: User, listBoards: ArrayList<Board>): BoardListFragment {
            return BoardListFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_USER, user)
                    putParcelableArrayList(KEY_BOARDS, listBoards)
                }
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.main_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.item_logout) {
            viewModel.logout()
            findTopNavController().navigate(R.id.loginFragment, null, navOptions {
                popUpTo(R.id.tabsFragment) {
                    inclusive = true
                }
            })
        }
        return true
    }

}