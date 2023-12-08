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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.taskscheduler.ChooseAvatarOptionFragment
import com.example.taskscheduler.R
import com.example.taskscheduler.databinding.FragmentRegistrationBinding
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.presentation.TakePhotoActivity
import com.example.taskscheduler.presentation.UserAuthState
import com.example.taskscheduler.presentation.ViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.io.File


class RegistrationFragment : Fragment() {

    lateinit var directory: File
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentRegistrationBinding
    private lateinit var user: User
    private var uri: Uri? = null
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            viewModelFactory
        )[RegistrationViewModel::class.java]
    }

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
//        var file: File? = null

        val file = File(
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
        createDirectory()
        with(binding) {
            imageViewAvatar.setOnClickListener {
                ChooseAvatarOptionFragment().newInstance()
                    .show(childFragmentManager, "ChooseAvatarDialog")
//                pickImageFromGallery()
                val email = editTextEmailAddressRegistration.text.toString().trim()
                val password = editTextPasswordRegistration.text.toString().trim()
                val name = editTextPersonName.text.toString().trim()
                val lastName = editTextPersonLastName.text.toString().trim()
                if (email == "" || password == "" || name == "" || lastName == "") {
                    Toast.makeText(requireContext(), "Заполните все поля!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    buttonSignUp.isEnabled = false
                    lifecycleScope.launch {
                        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                            viewModel.signUp(email, password, name, lastName, uri).collect {
                                when (it) {
                                    is UserAuthState.Error -> Toast.makeText(
                                        this@RegistrationFragment.requireContext(),
                                        it.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    UserAuthState.Loading -> {
                                    }
                                    is UserAuthState.Success -> {
                                        user = it.user
                                        Toast.makeText(
                                            requireContext(),
                                            "Регистрация пользователя ${user.name} ${user.lastName} прошла успешно!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        launchWelcomeFragment(user)
                                    }
                                }
                            }
                        }
                    }


                }

            }
        }
        observeViewModel()

    }

    private fun observeViewModel() {

    }

    private fun launchWelcomeFragment(user: User) {
        findNavController().navigate(
            RegistrationFragmentDirections.actionRegistrationFragmentToWelcomeFragment(
                user
            )
        )
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