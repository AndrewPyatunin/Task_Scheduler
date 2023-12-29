package com.example.taskscheduler.presentation.userprofile

import android.app.Activity
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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navOptions
import com.bumptech.glide.Glide
import com.example.taskscheduler.R
import com.example.taskscheduler.databinding.FragmentUserProfileBinding
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.findTopNavController
import com.example.taskscheduler.presentation.TakePhotoActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class UserProfileFragment : Fragment() {

    lateinit var binding: FragmentUserProfileBinding
    val auth = Firebase.auth
    private var uri: Uri? = null
    var userInfo = ""
    lateinit var user: User
    private val viewModel by lazy {
        ViewModelProvider(this)[UserProfileViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val firebaseUser = auth.currentUser
        binding.textViewUserName.text = firebaseUser?.displayName
        if (firebaseUser?.photoUrl != null)
            Glide.with(this).load(firebaseUser.photoUrl).into(binding.profilePicture)
        onClick(binding.textViewLogout)
        onClick(binding.imageViewLogout)
        with(binding) {
            textViewEmail.text = firebaseUser?.email
            imageViewEditUserDescription.setOnClickListener {
                editTextPersonDescription.setText(textViewDescriptionUser.text)
                changeVisibilityForAllDescriptionElements()
                imageViewSaveDescription.setOnClickListener {
                    viewModel.updateUserProfile(editTextPersonDescription.text.toString(), "", user)
                }

            }
            imageViewEditUserEmail.setOnClickListener {
                editTextPersonEmail.setText(textViewEmail.text)
                changeVisibilityForAllEmailElements()
                imageViewSaveEmail.setOnClickListener {
                    viewModel.updateUserProfile("", editTextPersonEmail.text.toString(), user)
                }
            }
            textViewChangeAvatar.setOnClickListener {
                pickImageFromGallery()
            }
        }
        observeViewModel()
    }

    private fun changeVisibilityForAllEmailElements() {
        with(binding) {
            changeVisibility(textViewEmail)
            changeVisibility(imageViewEditUserEmail)
            changeVisibility(editTextPersonEmail)
            changeVisibility(imageViewSaveEmail)
        }
    }

    private fun changeVisibilityForAllDescriptionElements() {
        with(binding) {
            changeVisibility(textViewDescriptionUser)
            changeVisibility(imageViewEditUserDescription)
            changeVisibility(editTextPersonDescription)
            changeVisibility(imageViewSaveDescription)
        }
    }

    private fun observeViewModel() {
        viewModel.descriptionLiveData.observe(viewLifecycleOwner) {
            if (binding.textViewDescriptionUser.visibility != View.VISIBLE)
                changeVisibilityForAllDescriptionElements()
            if (it != null) binding.textViewDescriptionUser.text = it
        }
        viewModel.emailLiveData.observe(viewLifecycleOwner) {
            if (binding.textViewEmail.visibility != View.VISIBLE)
                changeVisibilityForAllEmailElements()
            binding.textViewEmail.text = it
        }
        viewModel.uriLiveData.observe(viewLifecycleOwner) {
            Glide.with(this).load(it).into(binding.profilePicture)
        }
        viewModel.userLiveData.observe(viewLifecycleOwner) {
            user = it
            userInfo = String.format(getString(R.string.full_name), it.name, it.lastName)
            viewModel.update(null, userInfo, user)
            binding.textViewUserName.text = userInfo
        }
    }

    private fun changeVisibility(view: View) {
        if (view.visibility == View.VISIBLE) {
            view.visibility = View.GONE
        } else view.visibility = View.VISIBLE
    }

    private fun onClick(view: View) {
        view.setOnClickListener {
            viewModel.updateStatus()
            auth.signOut()
            findTopNavController().navigate(R.id.loginFragment, null, navOptions {
                popUpTo(R.id.tabsFragment) {
                    inclusive = true
                }
            })
        }
    }

    private val takePhotoForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                uri = result.data?.data
                Toast.makeText(requireContext(), "$uri", Toast.LENGTH_SHORT).show()
            }
        }

    private val pickImageFromGalleryForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                uri = it.data?.data
                Toast.makeText(requireContext(), uri.toString(), Toast.LENGTH_SHORT).show()
                viewModel.update(uri, userInfo, user)
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
}