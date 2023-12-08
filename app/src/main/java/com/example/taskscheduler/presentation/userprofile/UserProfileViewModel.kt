package com.example.taskscheduler.presentation.userprofile

import android.app.Application
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.taskscheduler.domain.models.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class UserProfileViewModel(application: Application) : AndroidViewModel(application) {

    val auth = Firebase.auth
    private val databaseUsersRef = Firebase.database.getReference("Users")
    private val storageReference = Firebase.storage.reference
    val user = auth.currentUser

    private var urlToFile = ""

    private val _emailLiveData = MutableLiveData<String>()
    val emailLiveData: LiveData<String>
        get() = _emailLiveData

    private val _descriptionLiveData = MutableLiveData<String>()
    val descriptionLiveData: LiveData<String>
        get() = _descriptionLiveData

    private val _uriLiveData = MutableLiveData<Uri>()
    val uriLiveData: LiveData<Uri>
        get() = _uriLiveData

    private val _userLiveData = MutableLiveData<User>()
    val userLiveData: LiveData<User>
        get() = _userLiveData

    init {
        databaseUsersRef.child(user!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChildren()) {
                        val user = snapshot.getValue(User::class.java)
                        _descriptionLiveData.value = user?.description
                        _userLiveData.value = user!!
                    }


                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
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
                updateUserAvatar(it.result, name)
                urlToFile = it.result.toString()
                callback.onUrlCallback(urlToFile)
            } else {
                Toast.makeText(getApplication(), "${it.result}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun updateUserProfile(description: String, email: String) {
        val ref = databaseUsersRef.child(user!!.uid)
        if (description != "") {
            ref.child("description").setValue(description)
            _descriptionLiveData.value = description
        }
        if (email != "") {
            updateUserEmail(email, ref)
        }
    }

    private fun updateUserAvatar(uri: Uri?, name: String) {

        val profileUpdates = userProfileChangeRequest {
            if (uri != null) photoUri = uri
            displayName = name
        }


        user?.updateProfile(profileUpdates)
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    if (uri != null) _uriLiveData.value = uri!!
                    Toast.makeText(
                        getApplication(),
                        "Обновление данных пользователя прошло успешно",
                        Toast.LENGTH_SHORT
                    )
                        .show()

                }
            }
    }

    fun update(uri: Uri?, name: String) {
        if (uri != null) {
            upLoadUserAvatar(uri, name, object : UrlCallback {
                override fun onUrlCallback(url: String) {
                    databaseUsersRef.child(user!!.uid).child("uri").setValue(url)
                    _uriLiveData.value = uri!!
                    Toast.makeText(
                        getApplication(),
                        "Обновление данных пользователя прошло успешно",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

            })
        }

    }


    private fun updateUserEmail(email: String, ref: DatabaseReference) {
        user!!.updateEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        getApplication(), "Электронный адрес пользователя обновлен",
                        Toast.LENGTH_SHORT
                    ).show()
                    ref.child("email").setValue(email)
                    _emailLiveData.value = email
                }
            }
    }

    fun updateStatus() {
        if (user != null) {
            databaseUsersRef.child(user.uid).child("onlineStatus").setValue(false)
        }
    }

    interface UrlCallback {

        fun onUrlCallback(url: String)
    }
}