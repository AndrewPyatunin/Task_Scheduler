package com.example.taskscheduler.presentation.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.taskscheduler.R
import com.example.taskscheduler.databinding.FragmentLoginBinding
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.presentation.ViewModelFactory
import javax.inject.Inject

class LoginFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var binding: FragmentLoginBinding
    private var email = ""

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[LoginViewModel::class.java]
    }

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
        observeViewModel()
        binding.buttonLogin.setOnClickListener {
            val password = binding.editTextTextPassword.text.toString().trim()
            email = binding.editTextTextEmailAddress.text.toString().trim()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.login(email, password)
            } else
                Toast.makeText(
                    requireContext(), getString(R.string.fill_in_login_parameters),
                    Toast.LENGTH_SHORT
                ).show()

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

        viewModel.success.observe(viewLifecycleOwner) {
            viewModel.fetchUser()
        }

        viewModel.userLiveData.observe(viewLifecycleOwner) {
            launchWelcomeFragment(it)
        }

        viewModel.error.observe(viewLifecycleOwner) {
            if (it != null) Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun launchWelcomeFragment(user: User) {
        findNavController().navigate(
            LoginFragmentDirections.actionLoginFragmentToWelcomeFragment(
                user
            )
        )
    }

    private fun launchRegistrationFragment() {
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegistrationFragment())
    }

    private fun launchForgotPasswordFragment(email: String) {
        findNavController().navigate(
            LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment(
                email
            )
        )
    }

    companion object {

        @JvmStatic
        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }
}