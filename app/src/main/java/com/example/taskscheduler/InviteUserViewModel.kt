package com.example.taskscheduler

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.taskscheduler.domain.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class InviteUserViewModel : ViewModel() {
    lateinit var database: FirebaseDatabase
    private val auth = Firebase.auth
    private val firebaseDatabase = Firebase.database
    val databaseUsersReference = firebaseDatabase.getReference("Users")

    private val _user = MutableLiveData<FirebaseUser>()
    val user: LiveData<FirebaseUser>
        get() = _user

    private val _listUsers = MutableLiveData<List<User>>()
    val listUsers: LiveData<List<User>>
        get() = _listUsers

    init {
        auth.addAuthStateListener {
            _user.value = auth.currentUser
        }
        databaseUsersReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val usersFromDb = ArrayList<User>()
                for (dataSnapshot in snapshot.children) {
                    val user = dataSnapshot.getValue(User::class.java)
                    if (user != null && user.id != auth.currentUser?.uid) {
                        usersFromDb.add(user)
                    }
                }
                _listUsers.value = usersFromDb
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}