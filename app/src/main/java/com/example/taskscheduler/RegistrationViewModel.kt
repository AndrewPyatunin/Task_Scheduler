package com.example.taskscheduler

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.taskscheduler.domain.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RegistrationViewModel: ViewModel() {
    private val firebaseDatabase = Firebase.database
    val databaseUsersReference = firebaseDatabase.getReference("Users")

    private val _success = MutableLiveData<FirebaseUser>()
    val success: LiveData<FirebaseUser>
        get() = _success

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    val auth = Firebase.auth
    init {
        auth.addAuthStateListener {
            if (it.currentUser == null) {

            }
        }
    }

    fun signUp(email: String, password: String, name: String, lastName: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            val userId = it.user?.uid ?: return@addOnSuccessListener
            val user = User(userId, name, lastName, email, false, ArrayList())
            databaseUsersReference.child(userId).setValue(user)
            _success.value = it.user
        }.addOnFailureListener {
            _error.value = it.message
        }
    }
}