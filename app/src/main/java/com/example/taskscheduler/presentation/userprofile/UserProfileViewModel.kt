package com.example.taskscheduler.presentation.userprofile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.MyApp
import com.example.taskscheduler.MyDatabaseConnection
import com.example.taskscheduler.data.TaskDatabase
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.domain.usecases.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserProfileViewModel : ViewModel() {

    private val userRepository = MyApp.userRepository
    private val updateUserDataUseCase = UpdateUserDataUseCase(userRepository)
    private val updateUserProfileUseCase = UpdateUserProfileUseCase(userRepository)
    private val updateStatusUseCase = UpdateStatusUseCase(userRepository)
    private val getUserFromRoomUseCase = GetUserFromRoomUseCase(userRepository)
    private val clearAllDataInRoomUseCase = ClearAllDataInRoomUseCase(
        MyApp.inviteRepository,
        MyApp.boardRepository,
        MyApp.notesListRepository,
        MyApp.noteRepository,
        userRepository
    )
    val auth = Firebase.auth

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
        MyDatabaseConnection.userId?.let {
            viewModelScope.launch(Dispatchers.IO) {
                _userLiveData.postValue(getUserFromRoomUseCase.execute(it))
            }
        }
    }

    fun updateUserProfile(description: String, email: String, user: User) {
        viewModelScope.launch {
            updateUserProfileUseCase.execute(description, email, user)
        }
    }

    fun update(uri: Uri?, name: String, user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            updateUserDataUseCase.execute(uri, name, user)
        }

    }

    fun updateStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            clearAllDataInRoomUseCase.execute()
        }
        updateStatusUseCase.execute()
    }
}