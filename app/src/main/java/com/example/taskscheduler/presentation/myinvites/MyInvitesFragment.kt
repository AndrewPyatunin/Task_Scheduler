package com.example.taskscheduler.presentation.myinvites

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskscheduler.MyApp
import com.example.taskscheduler.databinding.FragmentMyInvitesBinding
import com.example.taskscheduler.domain.models.Invite
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.presentation.ViewModelFactory
import javax.inject.Inject

class MyInvitesFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var binding: FragmentMyInvitesBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyInvitesAdapter
    private lateinit var user: User
    private lateinit var invite: Invite
    private val component by lazy { (requireActivity().application as MyApp).component.fragmentComponent() }
    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[MyInvitesViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
        childFragmentManager.setFragmentResultListener(KEY_REQUEST_REPLY_DIALOG, this) {
            _, bundle ->
            when(bundle.getBoolean(KEY_BUNDLE_REPLY_DIALOG)) {
                true -> viewModel.acceptInvite(user, invite)
                false -> viewModel.declineInvite(user, invite)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyInvitesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        initViews()
        adapter.onItemClick = {
            invite = it
            ReplyToInviteDialogFragment.newInstance(it).show(childFragmentManager, "ReplyDialog")
        }
    }

    private fun initViews() {
        recyclerView = binding.recyclerViewMyInvites
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter = MyInvitesAdapter()
        recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.invitesLiveData.observe(viewLifecycleOwner) {
            adapter.invitesList = it
            binding.progressBarInvites.visibility = View.GONE
        }

        viewModel.user.observe(viewLifecycleOwner) {
            user = it
            viewModel.getInvitesFromRoom()
        }

        viewModel.invitesReady.observe(viewLifecycleOwner) {
        }
    }

    fun okClicked(invite: Invite) {
        viewModel.acceptInvite(user, invite)
    }

    fun cancelClicked(invite: Invite) {
        viewModel.declineInvite(user, invite)
    }


    companion object {

        const val KEY_BUNDLE_REPLY_DIALOG = "bundle_reply_dialog"
        const val KEY_REQUEST_REPLY_DIALOG = "request_reply_dialog"

        const val KEY_USER = "user"

        fun newInstance(user: User): MyInvitesFragment {

            return MyInvitesFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_USER, user)
                }
            }
        }
    }

}