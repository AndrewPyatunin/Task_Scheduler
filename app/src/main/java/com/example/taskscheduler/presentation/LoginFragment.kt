package com.example.taskscheduler.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.taskscheduler.LoginViewModel
import com.example.taskscheduler.R
import com.example.taskscheduler.databinding.FragmentLoginBinding
import com.example.taskscheduler.domain.User
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {
    lateinit var auth: FirebaseAuth
    lateinit var binding : FragmentLoginBinding
    lateinit var user: User
    private var email = ""
    lateinit var viewModel: LoginViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        observeViewModel()
//        auth.signOut()
        binding.buttonLogin.setOnClickListener {
            val password = binding.editTextTextPassword.text.toString().trim()
            email = binding.editTextTextEmailAddress.text.toString().trim()
            viewModel.login(email, password)
        }
        binding.textViewRegistr.setOnClickListener {
            launchRegistrationFragment()
        }
        binding.textViewForgotPassword.setOnClickListener {
            email = binding.editTextTextEmailAddress.text.toString().trim()
            launchForgotPasswordFragment(email)
        }
    }

    fun observeViewModel() {
        viewModel.error.observe(viewLifecycleOwner, Observer {
            if (it != null) Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        })
        viewModel.success.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                launchBoardListFragment(it.uid)
            }
        })
    }

    fun launchRegistrationFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, RegistrationFragment.newInstance())
            .addToBackStack(null)
            .commit()
    }

    fun launchForgotPasswordFragment(email: String) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, ForgotPasswordFragment.newInstance(email))
            .addToBackStack(null)
            .commit()
    }

    fun launchBoardListFragment(userId: String) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, BoardListFragment.newInstance(userId))
            .addToBackStack(BoardListFragment.NAME_BOARD_LIST)
            .commit()
    }


    companion object {
        @JvmStatic
        fun newInstance() : LoginFragment {
            val fragment = LoginFragment()
//            val args = Bundle().apply {
//                putString("password", password)
//                putString("email", email)
//            }
//            fragment.arguments = args
            return fragment
        }
    }
}