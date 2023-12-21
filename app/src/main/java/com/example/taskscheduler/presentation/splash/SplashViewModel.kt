package com.example.taskscheduler.presentation.splash

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.taskscheduler.domain.models.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashViewModel : ViewModel() {

    val auth = Firebase.auth

    private val _firebaseUser = MutableLiveData<FirebaseUser>()
    val firebaseUser: LiveData<FirebaseUser>
        get() = _firebaseUser

    init {
        _firebaseUser.value = auth.currentUser
        val userList = listOf(User(name = "Andrey"), User(name = "Alex"), User(name = "Helen"), User(name = "Evgeny", lastName = "Vladimirovich"), User(name = "Nataly"), User(name = "Anna"))
        val list = userList.asSequence().filter { it.lastName != "" }.toList()//forEach { Log.i("SPLASH_USERS", it.toString()) }
    }
}
