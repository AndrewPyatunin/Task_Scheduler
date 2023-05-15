package com.example.taskscheduler.presentation

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.taskscheduler.RegistrationViewModel
import com.example.taskscheduler.databinding.FragmentRegistrationBinding
import com.example.taskscheduler.domain.ListOfBoards
import com.example.taskscheduler.domain.User
import com.google.firebase.auth.FirebaseAuth

class RegistrationFragment: Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentRegistrationBinding
    private lateinit var viewModel: RegistrationViewModel
    private lateinit var user: User
    private var uri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val takePhotoForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                uri = result.data?.data
                Toast.makeText(requireContext(), "$uri", Toast.LENGTH_SHORT).show()
//                if (data?.getBooleanExtra(App.IS_SIGN_OUT, false) == true) {
//                    // exit fun
//                }
            }
        }

    private val pickImageFromGalleryForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                uri = it.data?.data
                Toast.makeText(requireContext(), uri.toString(), Toast.LENGTH_SHORT).show()
            }
        }

    private fun startTakePhotoActivity() {
        takePhotoForResult.launch(Intent(requireActivity(), TakePhotoActivity::class.java))
    }

    private fun pickImageFromGallery() {
        val pickIntent = Intent(Intent.ACTION_PICK)
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        pickImageFromGalleryForResult.launch(pickIntent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[RegistrationViewModel::class.java]
        binding.imageViewAvatar.setOnClickListener {
            pickImageFromGallery()
        }
        binding.buttonSignUp.setOnClickListener {
            val email = binding.editTextEmailAddressRegistration.text.toString().trim()
            val password = binding.editTextPasswordRegistration.text.toString().trim()
            val name = binding.editTextPersonName.text.toString().trim()
            val lastName = binding.editTextPersonLastName.text.toString().trim()
            viewModel.signUp(email, password, name, lastName, uri)

        }
        observeViewModel()

    }

    private fun observeViewModel() {
        viewModel.error.observe(viewLifecycleOwner, Observer {
            if (it != null) Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        })
        viewModel.success.observe(viewLifecycleOwner, Observer {
            if (it != null) {
//                launchBoardListFragment(viewModel.user.value)
            }
        })
        viewModel.user.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                user = it
                launchBoardListFragment(it)
            }
        })
    }

    private fun launchBoardListFragment(user: User) {
        findNavController().navigate(RegistrationFragmentDirections.actionRegistrationFragmentToBoardListFragment(user, ListOfBoards(ArrayList())))
//        requireActivity().supportFragmentManager.beginTransaction()
//            .replace(R.id.fragment_container, BoardListFragment.newInstance(user, ArrayList()))
//            .addToBackStack(BoardListFragment.NAME_BOARD_LIST)
//            .commit()
    }

    companion object {
        const val NAME_LAUNCH = "launchBoardListFragment"
        fun newInstance(): RegistrationFragment {
            return RegistrationFragment()
        }
    }
}