package com.example.taskscheduler.presentation.registration

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.taskscheduler.R
import com.example.taskscheduler.databinding.FragmentRegistrationBinding
import com.example.taskscheduler.domain.User
import com.example.taskscheduler.presentation.TakePhotoActivity
import com.google.firebase.auth.FirebaseAuth
import java.io.File


class RegistrationFragment: Fragment() {
    lateinit var directory: File
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
//                uri = generateFileUri()
                Toast.makeText(requireContext(), "$uri", Toast.LENGTH_SHORT).show()
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

    private fun takePhotoFromCamera() {
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        intent.putExtra(MediaStore.EXTRA_OUTPUT, generateFileUri());
        takePhotoForResult.launch(intent)
//        startActivityForResult(intent, CAMERA)
    }

    private fun generateFileUri(): Uri? {
        var file: File? = null

        file = File(
            directory.path + "/" + "photo_"
                    + System.currentTimeMillis() + ".jpg"
        )
        Log.d("USER_TAG", "fileName = $file")
//        return FileProvider.getUriForFile(
//            requireContext(),
//            "com.example.taskscheduler.provider",
//            file
//        )
        return Uri.fromFile(file)
    }

    private fun createDirectory() {
        directory = File(
            Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "MyFolder"
        )
        if (!directory.exists()) directory.mkdirs()
    }

    private fun pickImageFromGallery() {
        val pickIntent = Intent(Intent.ACTION_PICK)
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        pickImageFromGalleryForResult.launch(pickIntent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[RegistrationViewModel::class.java]
        createDirectory()
        binding.imageViewAvatar.setOnClickListener {
//            ChooseAvatarOptionFragment().newInstance().show(childFragmentManager, "ChooseAvatarDialog")
            pickImageFromGallery()
        }
        binding.buttonSignUp.setOnClickListener {
            val email = binding.editTextEmailAddressRegistration.text.toString().trim()
            val password = binding.editTextPasswordRegistration.text.toString().trim()
            val name = binding.editTextPersonName.text.toString().trim()
            val lastName = binding.editTextPersonLastName.text.toString().trim()
            if (email == "" || password == "" || name == "" || lastName == "") {
                Toast.makeText(requireContext(), "Заполните все поля!", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.signUp(email, password, name, lastName, uri)
            }

        }
        observeViewModel()

    }

    private fun observeViewModel() {
        viewModel.error.observe(viewLifecycleOwner, Observer {
            if (it != null) Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        })
        viewModel.success.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                launchWelcomeFragment()
//                launchBoardListFragment(viewModel.user.value)
            }
        })
        viewModel.user.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                user = it
//                launchBoardListFragment(it)

            }

        })
    }

    private fun launchWelcomeFragment() {
        val userName = String.format(getString(R.string.full_name), user.name, user.lastName)
        findNavController().navigate(RegistrationFragmentDirections.actionRegistrationFragmentToWelcomeFragment(userName))
    }

    private fun launchBoardListFragment(user: User) {
        findNavController().navigate(RegistrationFragmentDirections.actionRegistrationFragmentToTabsFragment())
//        requireActivity().supportFragmentManager.beginTransaction()
//            .replace(R.id.fragment_container, BoardListFragment.newInstance(user, ArrayList()))
//            .addToBackStack(BoardListFragment.NAME_BOARD_LIST)
//            .commit()
    }

    fun galleryClicked() {
        pickImageFromGallery()
    }

    fun cameraClicked() {
        takePhotoFromCamera()
    }

    companion object {
        const val NAME_LAUNCH = "launchBoardListFragment"
        fun newInstance(): RegistrationFragment {
            return RegistrationFragment()
        }
    }
}