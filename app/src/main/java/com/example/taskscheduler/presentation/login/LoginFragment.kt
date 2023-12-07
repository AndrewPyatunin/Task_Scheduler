package com.example.taskscheduler.presentation.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import com.example.taskscheduler.databinding.FragmentLoginBinding
import com.example.taskscheduler.domain.Delegate
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.presentation.UserAuthState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

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
                lifecycleScope.launch {
                    repeatOnLifecycle(Lifecycle.State.RESUMED) {
                        viewModel.getUser(it.uid).collect { state ->
                            when(state) {
                                is UserAuthState.Error -> Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                                UserAuthState.Loading -> {

                                }
                                is UserAuthState.Success -> launchWelcomeFragment(state.user)
                            }

                        }
                    }
                }
            }
        })
    }

    private fun launchWelcomeFragment(user: User) {
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToWelcomeFragment(user))
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