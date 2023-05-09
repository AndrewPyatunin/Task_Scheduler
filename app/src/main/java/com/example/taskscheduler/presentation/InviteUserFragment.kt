package com.example.taskscheduler.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskscheduler.BoardListAdapter
import com.example.taskscheduler.InviteUserAdapter
import com.example.taskscheduler.InviteUserViewModel
import com.example.taskscheduler.R
import com.example.taskscheduler.databinding.FragmentInviteUserBinding
import com.example.taskscheduler.domain.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class InviteUserFragment : Fragment(){
    lateinit var binding: FragmentInviteUserBinding
    lateinit var recyclerViewUser: RecyclerView
    lateinit var userAdapter: InviteUserAdapter
    val listForInvite = ArrayList<User>()
    lateinit var viewModel: InviteUserViewModel



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
        viewModel = ViewModelProvider(this)[InviteUserViewModel::class.java]
        initViews()
        observeViewModel()
        userAdapter.onItemClick = {
            val checkBox = requireActivity().findViewById<CheckBox>(R.id.checkBoxInvited)
            checkBox.isChecked = true
            listForInvite.add(it)
            Log.i("USER_INVITE_LIST", listForInvite.joinToString { it -> it.toString() })
        }
    }

    fun initViews() {
        recyclerViewUser = binding.recyclerViewInviteUser
        recyclerViewUser.layoutManager = GridLayoutManager(requireContext(), 1)
//        recyclerViewBoardList.isNestedScrollingEnabled = false
        userAdapter = InviteUserAdapter()
        recyclerViewUser.adapter = userAdapter

    }


    fun observeViewModel() {
        viewModel.user.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, LoginFragment.newInstance())
                    .addToBackStack(null)
                    .commit()
            }
        })
        viewModel.listUsers.observe(viewLifecycleOwner, Observer {
            userAdapter.users = it as ArrayList<User>
        })
    }

    companion object {
        fun newInstance(): InviteUserFragment {
            return InviteUserFragment()
        }
    }
}