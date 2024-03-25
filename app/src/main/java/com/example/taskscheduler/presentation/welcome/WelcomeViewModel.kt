package com.example.taskscheduler.presentation.welcome

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.MyApp
import com.example.taskscheduler.MyDatabaseConnection
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.domain.usecases.GetUserFromRoomUseCase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class WelcomeViewModel @Inject constructor(
    private val getUserFromRoomUseCase: GetUserFromRoomUseCase
) : ViewModel() {

    private val auth = Firebase.auth

    private val _launchReady = MutableLiveData<User>()
    val launchReady: LiveData<User>
        get() = _launchReady

    private val _usersReady = MutableLiveData<Unit>()
    val usersReady: LiveData<Unit> = _usersReady

    init {
        viewModelScope.launch(Dispatchers.IO) {
            MyDatabaseConnection.onCallbackReady()
            _usersReady.postValue(Unit)
        }
    }

    fun getUser() {
        viewModelScope.launch(Dispatchers.IO) {
            _launchReady.postValue((auth.currentUser?.uid ?: MyDatabaseConnection.userId)?.let {
                getUserFromRoomUseCase.execute(it)
            })
        }
    }
}