package com.example.taskscheduler.presentation

import android.util.Log
import androidx.lifecycle.*
import com.example.taskscheduler.MyApp
import com.example.taskscheduler.MyDatabaseConnection
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.domain.usecases.AddAllUsersUseCase
import com.example.taskscheduler.domain.usecases.GetUserFromRoomUseCase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WelcomeViewModel : ViewModel() {

    private val userRepository = MyApp.userRepository
    private val addAllUsersUseCase = AddAllUsersUseCase(userRepository)
    private val getUserFromRoomUseCase = GetUserFromRoomUseCase(userRepository)
    private val auth = Firebase.auth

    private val _launchReady = MutableLiveData<User>()
    val launchReady: LiveData<User>
        get() = _launchReady

    private val _usersReady = MutableLiveData<Unit>()
    val usersReady: LiveData<Unit> = _usersReady

    init {
        viewModelScope.launch(Dispatchers.IO) {
            MyDatabaseConnection.onCallbackReady()
//            MyDatabaseConnection.query(application, this)
//            _usersReady.postValue(addAllUsersUseCase.execute(viewModelScope))
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