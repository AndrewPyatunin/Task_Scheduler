package com.example.taskscheduler.presentation

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskscheduler.BoardListAdapter
import com.example.taskscheduler.BoardListViewModel
import com.example.taskscheduler.R
import com.example.taskscheduler.databinding.FragmentBoardListBinding
import com.example.taskscheduler.domain.Board
import com.example.taskscheduler.domain.User
import com.squareup.picasso.Picasso

class BoardListFragment: Fragment(), MenuProvider {
    private var _binding: FragmentBoardListBinding? = null
    private val binding: FragmentBoardListBinding
        get() = _binding ?: throw RuntimeException("FragmentBoardListBinding==null")
    private lateinit var userId: String
    lateinit var user: User
    private lateinit var viewModel: BoardListViewModel
    lateinit var recyclerViewBoardList: RecyclerView
    lateinit var boardsAdapter: BoardListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArgs()
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

        viewModel = ViewModelProvider(this)[BoardListViewModel::class.java]
        observeViewModel()
        initViews()
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        user = viewModel.user.value ?: User()
        Log.i("USER_URL_LIST", user.uri)
        binding.textViewWelcomeUser.text = String.format(getString(R.string.welcome_user, user.name, user.lastName))
        Picasso.get().load(user.uri).resize(50, 50).centerCrop().into(binding.imageViewUserAvatarBoardList)

        binding.imageView.setOnClickListener {
            Log.i("USER_BOARD_LIST", viewModel.user.value.toString())
            viewModel.user.value?.let { it1 -> launchNewBoardFragment(it1) }
        }
        boardsAdapter.onItemClick = {
            launchBoardFragment(it)
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun observeViewModel() {
        viewModel.firebaseUser.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                launchLoginFragment()
            }
        })
        viewModel.boardList.observe(viewLifecycleOwner, Observer {
            boardsAdapter.boards = it
        })
        viewModel.user.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                user = it
            }
        })
    }

    fun initViews() {
        recyclerViewBoardList = binding.recyclerViewBoardList
        recyclerViewBoardList.layoutManager = GridLayoutManager(requireContext(), 2)
//        recyclerViewBoardList.isNestedScrollingEnabled = false
        boardsAdapter = BoardListAdapter()
        recyclerViewBoardList.adapter = boardsAdapter
    }

    fun launchLoginFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, LoginFragment.newInstance())
            .addToBackStack(null)
            .commit()
    }

    fun launchNewBoardFragment(user: User) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, NewBoardFragment.newInstance(user))
            .addToBackStack(null)
            .commit()
    }

    fun launchBoardFragment(board: Board) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, BoardFragment.newInstance(board))
            .addToBackStack(null)
            .commit()
    }

    private fun parseArgs() {
        requireArguments().getParcelable<User>(KEY_USER)?.let {
            user = it
        }
    }


    companion object {

        const val NAME_BOARD_LIST = "BoardListFragment"

        private const val KEY_USER = "user"

        fun newInstance(user: User): BoardListFragment {
            return BoardListFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_USER, user)
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
        }
        return true
    }

}