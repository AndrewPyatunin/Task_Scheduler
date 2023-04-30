package com.example.taskscheduler.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.taskscheduler.R
import com.example.taskscheduler.RegistrationViewModel
import com.example.taskscheduler.databinding.FragmentRegistrationBinding
import com.example.taskscheduler.domain.User
import com.google.firebase.auth.FirebaseAuth

class RegistrationFragment: Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentRegistrationBinding
    private lateinit var viewModel: RegistrationViewModel
    private lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[RegistrationViewModel::class.java]
        binding.buttonSignUp.setOnClickListener {
            val email = binding.editTextEmailAddressRegistration.text.toString().trim()
            val password = binding.editTextPasswordRegistration.text.toString().trim()
            val name = binding.editTextPersonName.text.toString().trim()
            val lastName = binding.editTextPersonLastName.text.toString().trim()
            viewModel.signUp(email, password, name, lastName)

        }
        observeViewModel()

    }

    fun observeViewModel() {
        viewModel.error.observe(viewLifecycleOwner, Observer {
            if (it != null) Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        })
        viewModel.success.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                launchBoardListFragment(user)
            }
        })
        viewModel.user.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                user = it
            }
        })
    }

    fun launchBoardListFragment(user: User) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, BoardListFragment.newInstance(user.id))
            .addToBackStack(BoardListFragment.NAME_BOARD_LIST)
            .commit()
    }

    companion object {
        const val NAME_LAUNCH = "launchBoardListFragment"
        fun newInstance(): RegistrationFragment {
            return RegistrationFragment()
        }
    }
}