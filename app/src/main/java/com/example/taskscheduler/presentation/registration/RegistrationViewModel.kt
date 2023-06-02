package com.example.taskscheduler.presentation.registration

import android.app.Application
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.taskscheduler.R
import com.example.taskscheduler.domain.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class RegistrationViewModel(application: Application) : AndroidViewModel(application) {
    private val firebaseDatabase = Firebase.database
    private var urlToFile = ""
    val databaseUsersReference = firebaseDatabase.getReference("Users")
    private val storageReference = Firebase.storage.reference

    private val _success = MutableLiveData<FirebaseUser>()
    val success: LiveData<FirebaseUser>
        get() = _success

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    val auth = Firebase.auth

    init {

        auth.addAuthStateListener {
            if (it.currentUser == null) {

            }
        }
    }

    private fun upLoadUserAvatar(uri: Uri, name: String, callback: UrlCallback) {
//        val imageStorage = storageReference.child("images")
        val imageRef = storageReference.child("images/${uri.lastPathSegment}")
        imageRef.putFile(uri).continueWithTask {
            if (!it.isSuccessful) {
                it.exception?.let { exception ->
                    throw exception
                }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener {
            Log.i("USER_URL", it.result.toString())
            if (it.isSuccessful) {
                updateUserProfile(it.result, name)
                urlToFile = it.result.toString()
                callback.onUrlCallback(urlToFile)
            } else {
                Toast.makeText(getApplication(), "${it.result}", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun updateUserProfile(uri: Uri, name: String) {
        val user = auth.currentUser
        val profileUpdates = userProfileChangeRequest {
            photoUri = uri
            displayName = name
        }


        user?.updateProfile(profileUpdates)
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(
                        getApplication(),
                        "Обновление данных пользователя прошло успешно",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    _success.value = auth.currentUser
                }
            }
    }


    fun signUp(email: String, password: String, name: String, lastName: String, uri: Uri?) {
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            val userId = it.user?.uid ?: return@addOnSuccessListener
            if (uri != null) {
                upLoadUserAvatar(uri, "$name $lastName", object : UrlCallback {
                    override fun onUrlCallback(url: String) {
                        val user = User(userId, name, lastName, email, true, emptyList(), url)
                        databaseUsersReference.child(userId).setValue(user)
                        _user.value = user

                    }

                })

            }

        }.addOnFailureListener {
            _error.value = it.message
        }
    }

    interface UrlCallback {
        fun onUrlCallback(url: String)
    }
}