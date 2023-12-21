package com.example.taskscheduler.presentation.registration

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.domain.usecases.AddUserUseCase
import com.example.taskscheduler.domain.usecases.RegistrationUseCase
import com.example.taskscheduler.presentation.UserAuthState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class RegistrationViewModel @Inject constructor(
    private val registrationUseCase: RegistrationUseCase,
    private val addUserUseCase: AddUserUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {

    private var _userFlow = MutableSharedFlow<UserAuthState>()
    val userFlow: Flow<UserAuthState> = _userFlow.asSharedFlow()

    suspend fun addUserToRoom(user: User) = addUserUseCase.execute(user)


    suspend fun signUp(
        email: String,
        password: String,
        name: String,
        lastName: String,
        uri: Uri?
    ): Flow<UserAuthState> = flow {
        auth.addAuthStateListener {
            if (it.currentUser == null) return@addAuthStateListener
        }
        _userFlow.emit(UserAuthState.Success(registrationUseCase.execute(email, password, name, lastName, uri, viewModelScope)))
    }

}