package com.example.taskscheduler.presentation.inviteuser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskscheduler.R
import com.example.taskscheduler.databinding.FragmentInviteUserBinding
import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.presentation.ViewModelFactory
import javax.inject.Inject

class InviteUserFragment : Fragment() {

    @Inject
    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var binding: FragmentInviteUserBinding
    private lateinit var recyclerViewUser: RecyclerView
    private lateinit var board: Board
    private lateinit var user: User
    private var userAdapter: InviteUserAdapter? = null
    private val listForInvite = ArrayList<User>()
    private val args by navArgs<InviteUserFragmentArgs>()

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[InviteUserViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArgs()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInviteUserBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getUsersForInvite(board)
        observeViewModel()
        initViews()
        userAdapter?.onItemClick = {
            if (it !in listForInvite) {
                listForInvite.add(it)
            } else if (it in listForInvite) {
                listForInvite.remove(it)
            }
        }
        binding.buttonInviteUser.setOnClickListener {
            listForInvite.forEach {
                viewModel.inviteUser(it, user, board)
            }
        }
    }

    private fun initViews() {
        recyclerViewUser = binding.recyclerViewInviteUser
        recyclerViewUser.layoutManager = GridLayoutManager(requireContext(), 1)
        userAdapter = InviteUserAdapter()
        recyclerViewUser.adapter = userAdapter
    }

    private fun parseArgs() {
        board = args.board
        user = args.user
    }

    private fun observeViewModel() {
        viewModel.listUsers.observe(viewLifecycleOwner) {
            userAdapter?.users = it as ArrayList<User>
            with(binding) {
                buttonInviteUser.visibility = View.VISIBLE
                pleaseWaitTextViewInvite.visibility = View.GONE
                loadingIndicatorInvite.visibility = View.GONE
            }
            recyclerViewUser.visibility = View.VISIBLE
            recyclerViewUser.adapter = userAdapter
        }


        viewModel.success.observe(viewLifecycleOwner) {
            viewModel.getUsersForInvite(board)
            Toast.makeText(requireContext(), getString(R.string.success_invite), Toast.LENGTH_SHORT)
                .show()
        }
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