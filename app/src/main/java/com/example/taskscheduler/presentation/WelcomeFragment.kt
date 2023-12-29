package com.example.taskscheduler.presentation

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.taskscheduler.R
import com.example.taskscheduler.databinding.FragmentWelcomeBinding
import com.example.taskscheduler.findTopNavController

class WelcomeFragment : Fragment() {

    lateinit var binding: FragmentWelcomeBinding
    lateinit var name: String
    private lateinit var viewModel: WelcomeViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[WelcomeViewModel::class.java]
        observeViewModel()
        Handler(Looper.getMainLooper()).postDelayed({ launchTabsFragment() }, 3000)
    }

    private fun observeViewModel() {
        viewModel.launchReady.observe(viewLifecycleOwner) { user ->
            name = "${user.name} ${user.lastName}"
            binding.textViewWelcome.text = String.format(getString(R.string.welcome), name)
            binding.textViewWelcome.visibility = View.VISIBLE
        }
        viewModel.usersReady.observe(viewLifecycleOwner) {
            viewModel.getUser()
        }
    }

    private fun launchTabsFragment() {
        findTopNavController().navigate(WelcomeFragmentDirections.actionWelcomeFragmentToTabsFragment())
    }
}