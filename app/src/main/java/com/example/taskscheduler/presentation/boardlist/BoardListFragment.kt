package com.example.taskscheduler.presentation.boardlist

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.taskscheduler.R
import com.example.taskscheduler.databinding.FragmentBoardListBinding
import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.findTopNavController
import com.example.taskscheduler.presentation.ViewModelFactory
import kotlinx.coroutines.launch
import javax.inject.Inject

class BoardListFragment : Fragment(), MenuProvider {

    private var _binding: FragmentBoardListBinding? = null
    private val binding: FragmentBoardListBinding
        get() = _binding ?: throw RuntimeException("FragmentBoardListBinding==null")
    lateinit var user: User
    var boardList = ArrayList<Board>()

    @Inject
    private lateinit var viewModelFactory: ViewModelFactory
    lateinit var recyclerViewBoardList: RecyclerView
    lateinit var boardsAdapter: BoardListAdapter
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            viewModelFactory
        )[BoardListViewModel::class.java]
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
        observeViewModel()
        initViews()
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        binding.imageViewAddNewBoard.setOnClickListener {
            launchNewBoardFragment(user)
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
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {//flow будет вызван, когда фрагмент перейдет в состояние Resumed и завершится в onPause
                viewModel.userFlow?.collect {
                    user = it
                    Glide.with(this@BoardListFragment).load(it.uri).centerCrop()
                        .into(binding.imageViewUserAvatarBoardList)
                    with(binding.textViewWelcomeUser) {
                        text =
                            String.format(
                                getString(
                                    R.string.welcome_user,
                                    user.name,
                                    user.lastName
                                )
                            )
                        visibility = View.VISIBLE
                    }
                }
                viewModel.getBoardsFlow(user).collect { list ->
                    boardsAdapter.boards = list
                    with(binding) {
                        loadingIndicator.visibility = View.GONE
                        pleaseWaitTextView.visibility = View.GONE
                        recyclerViewBoardList.visibility = View.VISIBLE
                    }
                }
            }
        }

    }

    private fun initViews() {
        recyclerViewBoardList = binding.recyclerViewBoardList
        recyclerViewBoardList.layoutManager = GridLayoutManager(requireContext(), 2)
        boardsAdapter = BoardListAdapter()
        if (boardList.isNotEmpty()) boardsAdapter.boards = boardList
        recyclerViewBoardList.adapter = boardsAdapter
    }

    private fun launchNewBoardFragment(user: User) {
        findNavController().navigate(
            BoardListFragmentDirections.actionBoardListFragmentToNewBoardFragment(
                user,
                Board()
            )
        )
    }

    private fun launchBoardFragment(board: Board) {
        findNavController().navigate(
            BoardListFragmentDirections.actionBoardListFragmentToOuterBoardFragment(
                user,
                board
            )
        )
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