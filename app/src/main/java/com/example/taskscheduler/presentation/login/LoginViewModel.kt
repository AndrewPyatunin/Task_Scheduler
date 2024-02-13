package com.example.taskscheduler.presentation.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.MyApp
import com.example.taskscheduler.MyDatabaseConnection
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.domain.usecases.GetUserFromRoomUseCase
import com.example.taskscheduler.domain.usecases.LogInUseCase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val getUserFromRoomUseCase = GetUserFromRoomUseCase(MyApp.userRepository)
    private val loginUseCase = LogInUseCase(MyApp.userAuthentication)
    private val auth = Firebase.auth

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private val _success = MutableLiveData<Unit>()
    val success: LiveData<Unit>
        get() = _success

    private val _userLiveData = MutableLiveData<User>()
    val userLiveData: LiveData<User>
        get() = _userLiveData

    fun fetchUser() {
        viewModelScope.launch(Dispatchers.IO) {
            _userLiveData.postValue(
                getUserFromRoomUseCase.execute(
                    auth.currentUser?.uid ?: MyDatabaseConnection.userId
                    ?: throw RuntimeException("User with that id is not found in Room")
                )
            )
        }

    }

    fun login(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _success.postValue(loginUseCase.execute(email, password, auth, viewModelScope))
            } catch (ex: Exception) { _error.postValue(ex.message) }
        }
    }
}