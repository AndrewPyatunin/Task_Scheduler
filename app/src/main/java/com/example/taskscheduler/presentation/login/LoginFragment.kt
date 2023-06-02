package com.example.taskscheduler.presentation.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.taskscheduler.databinding.FragmentLoginBinding
import com.example.taskscheduler.domain.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {
    private val auth = Firebase.auth
    lateinit var binding : FragmentLoginBinding
    lateinit var user: User
    private var email = ""
    lateinit var viewModel: LoginViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        observeViewModel()
        binding.buttonLogin.setOnClickListener {
            val password = binding.editTextTextPassword.text.toString().trim()
            email = binding.editTextTextEmailAddress.text.toString().trim()
            if (email != "" && password != "") {
                viewModel.login(email, password)
            } else
                Toast.makeText(requireContext(), "Заполните поля email и пароль!",
                    Toast.LENGTH_SHORT).show()

        }
        binding.textViewRegistr.setOnClickListener {
            launchRegistrationFragment()
        }
        binding.textViewForgotPassword.setOnClickListener {
            email = binding.editTextTextEmailAddress.text.toString().trim()
            launchForgotPasswordFragment(email)
        }
    }

    private fun observeViewModel() {
        viewModel.error.observe(viewLifecycleOwner, Observer {
            if (it != null) Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        })
        viewModel.success.observe(viewLifecycleOwner, Observer {
            if (it != null) {
//                launchTabsFragment()
                launchWelcomeFragment()
            }
        })
        viewModel.user.observe(viewLifecycleOwner, Observer {
            user = it
        })
    }

    private fun launchWelcomeFragment() {
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToWelcomeFragment(auth.currentUser?.displayName.toString()))
    }

    private fun launchRegistrationFragment() {
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegistrationFragment())
    }

    private fun launchForgotPasswordFragment(email: String) {
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment(email))
    }

    private fun launchTabsFragment() {
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToTabsFragment())
    }


    companion object {
        @JvmStatic
        fun newInstance() : LoginFragment {
            return LoginFragment()
        }
    }
}