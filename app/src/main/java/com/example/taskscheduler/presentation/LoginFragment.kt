package com.example.taskscheduler.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.taskscheduler.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {
    lateinit var auth: FirebaseAuth
    lateinit var binding : FragmentLoginBinding

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
        val password = binding.editTextTextPassword.text.toString().trim()
        val email = binding.editTextTextEmailAddress.text.toString().trim()
        auth = Firebase.auth
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val user = auth.currentUser

                } else {

                }
            }
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {

                } else {

                }
            }
        auth.signOut()
        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {

            } else {

            }
        }
        binding.buttonLogin.setOnClickListener {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (auth.currentUser != null) {

        }
    }

    companion object {
        @JvmStatic
        fun newInstance(password: String, email: String) : LoginFragment {
            val fragment = LoginFragment()
            val args = Bundle().apply {
                putString("password", password)
                putString("email", email)
            }
            fragment.arguments = args
            return fragment
        }
    }
}