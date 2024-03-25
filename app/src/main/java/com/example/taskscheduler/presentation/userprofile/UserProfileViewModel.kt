package com.example.taskscheduler.presentation.userprofile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.MyDatabaseConnection
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.domain.usecases.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserProfileViewModel @Inject constructor(
    private val updateUserDataUseCase: UpdateUserDataUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val updateStatusUseCase: UpdateStatusUseCase,
    private val getUserFromRoomUseCase: GetUserFromRoomUseCase,
    private val clearAllDataInRoomUseCase: ClearAllDataInRoomUseCase
) : ViewModel() {

    val auth = Firebase.auth

    private val _avatarLiveData = MutableLiveData<Unit>()
    val avatarLiveData: LiveData<Unit>
        get() = _avatarLiveData

    private val _updateLiveData = MutableLiveData<Unit>()
    val updateLiveData: LiveData<Unit>
        get() = _updateLiveData

    private val _userLiveData = MutableLiveData<User>()
    val userLiveData: LiveData<User>
        get() = _userLiveData

    init {
        getUser()
    }

    fun updateUserProfile(description: String, email: String, user: User) {
        viewModelScope.launch {
            _updateLiveData.postValue(updateUserProfileUseCase.execute(description, email, user, viewModelScope))
        }
    }

    fun getUser() {
        (auth.currentUser?.uid ?: MyDatabaseConnection.userId)?.let {
            viewModelScope.launch(Dispatchers.IO) {
                _userLiveData.postValue(getUserFromRoomUseCase.execute(it))
            }
        }
    }

    fun update(uri: Uri?, name: String, user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            _avatarLiveData.postValue(updateUserDataUseCase.execute(uri, name, user, viewModelScope))
        }
    }

    fun updateStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            updateStatusUseCase.execute()
            clearAllDataInRoomUseCase.execute()
        }
    }
}