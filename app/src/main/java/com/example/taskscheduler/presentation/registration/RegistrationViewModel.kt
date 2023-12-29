package com.example.taskscheduler.presentation.registration

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.MyApp
import com.example.taskscheduler.domain.usecases.RegistrationUseCase
import com.example.taskscheduler.presentation.UserAuthState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RegistrationViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    private val userAuthentication = MyApp.userAuthentication
    private val registrationUseCase: RegistrationUseCase = RegistrationUseCase(userAuthentication)

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
        emit(
            UserAuthState.Success(
                registrationUseCase.execute(
                    email,
                    password,
                    name,
                    lastName,
                    uri,
                    viewModelScope
                )
            )
        )
    }

}