package com.example.taskscheduler.presentation

import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.taskscheduler.R
import com.example.taskscheduler.databinding.FragmentWelcomeBinding
import com.example.taskscheduler.findTopNavController
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class WelcomeFragment : Fragment() {

    lateinit var binding: FragmentWelcomeBinding
    lateinit var firebaseUser: FirebaseUser
    lateinit var viewModel: WelcomeViewModel
    val args by navArgs<WelcomeFragmentArgs>()
    lateinit var name: String



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWelcomeBinding.inflate(inflater, container, false)
//        firebaseUser = Firebase.auth.currentUser!!
        val user = args.user
        name = "${user.name} ${user.lastName}"
        viewModel = ViewModelProvider(this)[WelcomeViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val handler = Handler(Looper.getMainLooper())
        binding.textViewWelcome.text = String.format(getString(R.string.welcome), name)
        handler.postDelayed( object : Runnable{
            override fun run() {
                launchTabsFragment()
            }

        }, 2000)
    }

    private fun observeViewModel() {
        viewModel.launchReady.observe(viewLifecycleOwner, Observer {
            launchTabsFragment()
        })
    }

    private fun launchTabsFragment() {
        findTopNavController().navigate(WelcomeFragmentDirections.actionWelcomeFragmentToTabsFragment())
    }
}