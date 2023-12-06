package com.example.taskscheduler.presentation.registration

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.domain.AddUserUseCase
import com.example.taskscheduler.domain.RegistrationUseCase
import com.example.taskscheduler.domain.User
import com.example.taskscheduler.presentation.UserAuthState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class RegistrationViewModel @Inject constructor(
    private val registrationUseCase: RegistrationUseCase,
    private val addUserUseCase: AddUserUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {

    private fun addUserToRoom(user: User) = addUserUseCase.execute(user)


    fun signUp(email: String, password: String, name: String, lastName: String, uri: Uri?): Flow<UserAuthState> {
        auth.addAuthStateListener {
            if (it.currentUser == null) return@addAuthStateListener
        }
        return registrationUseCase.execute(email, password, name, lastName, uri, viewModelScope).map {
            addUserToRoom(it)
            UserAuthState.Success(it) as UserAuthState
        }.onStart {
            emit(UserAuthState.Loading)
        }.catch {
            it.message?.let { message ->
                emit(UserAuthState.Error(message))
            }
        }

    }

}