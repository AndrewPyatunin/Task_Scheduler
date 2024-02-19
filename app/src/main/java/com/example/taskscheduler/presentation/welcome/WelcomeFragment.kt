package com.example.taskscheduler.presentation.welcome

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.taskscheduler.MyApp
import com.example.taskscheduler.R
import com.example.taskscheduler.databinding.FragmentWelcomeBinding
import com.example.taskscheduler.findTopNavController
import com.example.taskscheduler.presentation.ViewModelFactory
import javax.inject.Inject

class WelcomeFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var binding: FragmentWelcomeBinding
    private lateinit var name: String
    private val viewModel by viewModels<WelcomeViewModel>(factoryProducer = { viewModelFactory })
    private val component by lazy { (requireActivity().application as MyApp).component.fragmentComponent() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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