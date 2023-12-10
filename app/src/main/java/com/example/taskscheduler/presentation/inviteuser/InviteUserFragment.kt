package com.example.taskscheduler.presentation.inviteuser

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskscheduler.InviteUserAdapter
import com.example.taskscheduler.databinding.FragmentInviteUserBinding
import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.User

class InviteUserFragment : Fragment() {

    lateinit var binding: FragmentInviteUserBinding
    lateinit var recyclerViewUser: RecyclerView
    private var userAdapter: InviteUserAdapter? = null
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModelFactory = InviteUserViewModelFactory(board)
        viewModel = ViewModelProvider(this, viewModelFactory)[InviteUserViewModel::class.java]
        observeViewModel()
        initViews()
        if (userAdapter != null)
            userAdapter?.onItemClick = {
                if (it !in listForInvite) {
                    listForInvite.add(it)
                } else if (it in listForInvite) {
                    listForInvite.remove(it)
                }
                Log.i("USER_INVITE_LIST", listForInvite.joinToString { it.toString() })
            }
        binding.buttonInviteUser.setOnClickListener {
            for (userForInvite in listForInvite) {
                viewModel.inviteUser(userForInvite, user, board)
            }
        }
        binding.recyclerViewInviteUser.afterMeasured {
            Log.i("USER_RECYCLER", this.id.toString())
            with(binding) {
                buttonInviteUser.visibility = View.VISIBLE
                pleaseWaitTextViewInvite.visibility = View.GONE
                loadingIndicatorInvite.visibility = View.GONE
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

    inline fun <T : View> T.afterMeasured(crossinline f: T.() -> Unit) {
        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (measuredWidth > 0 && measuredHeight > 0) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    f()
                }
            }
        })
    }


    private fun observeViewModel() {
        viewModel.listUsers.observe(viewLifecycleOwner, Observer {
            userAdapter?.users = it as ArrayList<User>

            with(binding) {
                buttonInviteUser.visibility = View.VISIBLE
                pleaseWaitTextViewInvite.visibility = View.GONE
                loadingIndicatorInvite.visibility = View.GONE
            }
            recyclerViewUser.visibility = View.VISIBLE
            recyclerViewUser.adapter = userAdapter
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