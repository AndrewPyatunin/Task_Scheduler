package com.example.taskscheduler.presentation.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.taskscheduler.domain.usecases.AddUserUseCase
import com.example.taskscheduler.domain.usecases.GetUserFlowFromRoomUseCase
import com.example.taskscheduler.domain.usecases.GetUserFlowUseCase
import com.example.taskscheduler.presentation.UserAuthState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val getUserFlowUseCase: GetUserFlowUseCase,
    private val getUserFlowFromRoomUseCase: GetUserFlowFromRoomUseCase,
    private val addUserUseCase: AddUserUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private val _success = MutableLiveData<FirebaseUser>()
    val success: LiveData<FirebaseUser>
        get() = _success

    init {

        auth.addAuthStateListener {
            if (it.currentUser != null) {
                _success.value = it.currentUser
            }
        }

    }

    fun getUser(userId: String) = getUserFlowUseCase.execute(userId).map {
        addUserUseCase.execute(it)
        UserAuthState.Success(it) as UserAuthState
    }.onStart {
        emit(UserAuthState.Loading)
    }.catch {
        it.message?.let { message ->
            emit(UserAuthState.Error(message))
        }
    }

    fun getUserFromRoom(userId: String) = getUserFlowFromRoomUseCase.execute(userId)


    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {

        }.addOnFailureListener {
            _error.value = it.message
        }
    }


    fun logout() {
        auth.signOut()
    }
}