package com.example.taskscheduler.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.taskscheduler.R
import com.example.taskscheduler.databinding.FragmentForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ForgotPasswordFragment : Fragment() {

    private lateinit var binding: FragmentForgotPasswordBinding
    lateinit var auth: FirebaseAuth
    private var email = ""

    private val args by navArgs<ForgotPasswordFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.editTextEmailAddressForResetPassword.setText(email)
        binding.buttonResetPassword.setOnClickListener {
            email = binding.editTextEmailAddressForResetPassword.text.toString().trim()
            auth = Firebase.auth
            auth.sendPasswordResetEmail(email).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(
                        requireContext(), getString(R.string.forgot_password_email_send_letter), Toast.LENGTH_LONG
                    ).show()
                    findNavController().popBackStack()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArgs()
    }

    private fun parseArgs() {
        email = args.email
    }

    companion object {
        private const val EMAIL_KEY = "email"

        fun newInstance(email: String): ForgotPasswordFragment {
            return ForgotPasswordFragment().apply {
                arguments = Bundle().apply {
                    putString(EMAIL_KEY, email)
                }
            }
        }
    }
}