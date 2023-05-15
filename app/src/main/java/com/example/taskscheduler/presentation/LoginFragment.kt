package com.example.taskscheduler.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.taskscheduler.LoginViewModel
import com.example.taskscheduler.databinding.FragmentLoginBinding
import com.example.taskscheduler.domain.Board
import com.example.taskscheduler.domain.ListOfBoards
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

    private fun observeViewModel() {
        viewModel.error.observe(viewLifecycleOwner, Observer {
            if (it != null) Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        })
//        viewModel.success.observe(viewLifecycleOwner, Observer {
//            if (it != null) {
//                launchBoardListFragment(viewModel.user.value as User,  viewModel.boardList.value as ArrayList<Board>)
//            }
//        })
        viewModel.user.observe(viewLifecycleOwner, Observer {
            user = it
        })
        viewModel.boardList.observe(viewLifecycleOwner, Observer {
            if (viewModel.user.value != null && it != null)
                launchBoardListFragment(viewModel.user.value as User, it)
        })
    }

    private fun launchRegistrationFragment() {
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegistrationFragment())
//        requireActivity().supportFragmentManager.beginTransaction()
//            .replace(R.id.fragment_container, RegistrationFragment.newInstance())
//            .addToBackStack(null)
//            .commit()
    }

    private fun launchForgotPasswordFragment(email: String) {
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment(email))
//        requireActivity().supportFragmentManager.beginTransaction()
//            .replace(R.id.fragment_container, ForgotPasswordFragment.newInstance(email))
//            .addToBackStack(null)
//            .commit()
    }

    private fun launchBoardListFragment(user: User, listBoards: ArrayList<Board>) {
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToBoardListFragment(user, ListOfBoards(listBoards)))
//        requireActivity().supportFragmentManager.beginTransaction()
//            .replace(R.id.fragment_container, BoardListFragment.newInstance(user, listBoards))
//            .addToBackStack(BoardListFragment.NAME_BOARD_LIST)
//            .commit()
    }


    companion object {
        @JvmStatic
        fun newInstance() : LoginFragment {
            return LoginFragment()
        }
    }
}