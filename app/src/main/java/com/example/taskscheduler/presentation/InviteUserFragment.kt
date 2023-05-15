package com.example.taskscheduler.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskscheduler.*
import com.example.taskscheduler.databinding.FragmentInviteUserBinding
import com.example.taskscheduler.domain.Board
import com.example.taskscheduler.domain.User

class InviteUserFragment : Fragment(){
    lateinit var binding: FragmentInviteUserBinding
    lateinit var recyclerViewUser: RecyclerView
    lateinit var userAdapter: InviteUserAdapter
    private val listForInvite = ArrayList<User>()
    lateinit var viewModel: InviteUserViewModel
    lateinit var viewModelFactory: InviteUserViewModelFactory
    lateinit var board: Board
    lateinit var user: User

    private val args by navArgs<InviteUserFragmentArgs>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArgs()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInviteUserBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.setUserStatus(true)
    }

    override fun onPause() {
        super.onPause()
        viewModel.setUserStatus(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModelFactory = InviteUserViewModelFactory(board)
        viewModel = ViewModelProvider(this, viewModelFactory)[InviteUserViewModel::class.java]
        initViews()
        observeViewModel()
        userAdapter.onItemClick = {
            val checkBox = requireActivity().findViewById<CheckBox>(R.id.checkBoxInvited)
            if (it !in listForInvite) {
//                checkBox.isChecked = true
                listForInvite.add(it)
            } else if (it in listForInvite) {
//                checkBox.isChecked = false
                listForInvite.remove(it)
            }
            Log.i("USER_INVITE_LIST", listForInvite.joinToString { it -> it.toString() })
        }
        binding.buttonInviteUser.setOnClickListener {
            for (userForInvite in listForInvite) {
                viewModel.inviteUser(userForInvite, user,  board)
            }
        }
    }

    private fun initViews() {
        recyclerViewUser = binding.recyclerViewInviteUser
        recyclerViewUser.layoutManager = GridLayoutManager(requireContext(), 1)
//        recyclerViewBoardList.isNestedScrollingEnabled = false
        userAdapter = InviteUserAdapter()
        recyclerViewUser.adapter = userAdapter

    }

    private fun parseArgs() {
//        board = requireArguments().getParcelable<Board>(KEY_BOARD) ?: Board()
//        user = requireArguments().getParcelable<User>(KEY_USER) ?: User()
        board = args.board
        user = args.user
    }


    private fun observeViewModel() {
        viewModel.user.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                findNavController().navigate(InviteUserFragmentDirections.actionInviteUserFragmentToLoginFragment())
//                requireActivity().supportFragmentManager.beginTransaction()
//                    .replace(R.id.fragment_container, LoginFragment.newInstance())
//                    .addToBackStack(null)
//                    .commit()
            }
        })
        viewModel.listUsers.observe(viewLifecycleOwner, Observer {
            userAdapter.users = it as ArrayList<User>
        })

        viewModel.success.observe(viewLifecycleOwner, Observer {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        })
    }

    companion object {
        private const val KEY_BOARD = "board_invite"
        private const val KEY_USER = "board_user"

        fun newInstance(board: Board, user: User): InviteUserFragment {
            return InviteUserFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_BOARD, board)
                    putParcelable(KEY_USER, user)
                }
            }
        }
    }
}