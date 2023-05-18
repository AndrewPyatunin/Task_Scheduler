package com.example.taskscheduler.presentation.myinvites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskscheduler.MyInvitesAdapter
import com.example.taskscheduler.databinding.FragmentMyInvitesBinding
import com.example.taskscheduler.domain.Invite
import com.example.taskscheduler.domain.User
import com.example.taskscheduler.presentation.ReplyToInviteDialogFragment

class MyInvitesFragment : Fragment() {
    lateinit var binding: FragmentMyInvitesBinding
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: MyInvitesAdapter
    lateinit var viewModel: MyInvitesViewModel
    lateinit var user: User

//    private val args by navArgs<MyInvitesFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        user = args.user
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyInvitesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[MyInvitesViewModel::class.java]
        observeViewModel()
        initViews()
        adapter.onItemClick = {
//            findNavController().navigate(MyInvitesFragmentDirections.actionMyInvitesFragmentToReplyToInviteDialogFragment(it))
            ReplyToInviteDialogFragment.newInstance(it).show(childFragmentManager, "ReplyDialog")
        }
    }

    private fun initViews() {
        recyclerView = binding.recyclerViewMyInvites
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter = MyInvitesAdapter()
        recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.inviteList.observe(viewLifecycleOwner, Observer {
            adapter.invitesList = it
        })
        viewModel.user.observe(viewLifecycleOwner, Observer {
            user = it
        })
    }

//    private fun parseArgs(): User {
//        return requireArguments().getParcelable<User>(KEY_USER)!!
//    }

    fun okClicked(invite: Invite) {
        viewModel.acceptInvite(user, invite)
    }

    fun cancelClicked() {

    }

    companion object {
        const val KEY_USER = "user"

        fun newInstance(user: User): MyInvitesFragment {

            return MyInvitesFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_USER, user)
//                    putParcelable()
                }
            }
        }
    }

}