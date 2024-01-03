package com.example.taskscheduler.presentation.boardlist

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.taskscheduler.MyDatabaseConnection.boardList
import com.example.taskscheduler.R
import com.example.taskscheduler.databinding.FragmentBoardListBinding
import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.findTopNavController

class BoardListFragment : Fragment(), MenuProvider {

    private var _binding: FragmentBoardListBinding? = null
    private val binding: FragmentBoardListBinding
        get() = _binding ?: throw RuntimeException("FragmentBoardListBinding==null")
    private lateinit var user: User
    private lateinit var recyclerViewBoardList: RecyclerView
    private lateinit var boardsAdapter: BoardListAdapter
    private val viewModel by lazy {
        ViewModelProvider(this)[BoardListViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        viewModel.boardsLiveData.observe(viewLifecycleOwner) {
            Log.d("DataUpdate", "LiveData changed: $it") // Log the updated LiveData
            boardList = it
            boardsAdapter.boards = it
            with(binding) {
                loadingIndicator.visibility = View.GONE
                pleaseWaitTextView.visibility = View.GONE
                recyclerViewBoardList.visibility = View.VISIBLE
            }
        }

        viewModel.dataReady.observe(viewLifecycleOwner) {
            Log.d("DataUpdate", "Start_getting_boards")
            viewModel.getBoardsFlow(user)
        }

        viewModel.fetchUser()

        viewModel.userLiveData.observe(viewLifecycleOwner) {
            user = it
            Log.d("BOARD_LIST_ADAPTER", boardsAdapter.boards.size.toString())
            viewModel.fetchBoards(it, boardList)
            drawAvatar()
        }
    }

    private fun drawAvatar() {
        Glide.with(this@BoardListFragment).load(user.uri).centerCrop()
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

    private fun initViews() {
        recyclerViewBoardList = binding.recyclerViewBoardList
        recyclerViewBoardList.layoutManager = GridLayoutManager(requireContext(), 2)
        boardsAdapter = BoardListAdapter()
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
            viewModel.logout(user)
            findTopNavController().navigate(R.id.loginFragment, null, navOptions {
                popUpTo(R.id.tabsFragment) {
                    inclusive = true
                }
            })
        }
        return true
    }

}