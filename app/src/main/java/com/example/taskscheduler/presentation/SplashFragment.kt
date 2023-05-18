package com.example.taskscheduler.presentation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.taskscheduler.R
import com.example.taskscheduler.databinding.FragmentSplashBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashFragment : Fragment(R.layout.fragment_splash) {

    private lateinit var binding: FragmentSplashBinding
    val auth = Firebase.auth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        launchMainScreen(auth.currentUser != null)
    }

    private fun launchMainScreen(isSignedIn: Boolean) {
//        val db = Database.getDbConnection()
//        auth.currentUser?.let { db.query(it) }
//        val extras = ActivityNavigator.Extras.Builder()
//            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//            .build()
//        findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToMainActivity2(isSignedIn), extras)
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val args = MainActivityArgs(isSignedIn)
        intent.putExtras(args.toBundle())
        startActivity(intent)
    }
}