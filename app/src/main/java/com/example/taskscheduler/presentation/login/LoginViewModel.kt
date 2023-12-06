package com.example.taskscheduler.presentation.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.taskscheduler.MyDatabaseConnection
import com.example.taskscheduler.data.TaskDatabase
import com.example.taskscheduler.data.TaskDatabaseDao
import com.example.taskscheduler.domain.AddUserUseCase
import com.example.taskscheduler.domain.GetUserFlowUseCase
import com.example.taskscheduler.domain.User
import com.example.taskscheduler.presentation.UserAuthState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val dao: TaskDatabaseDao,
    private val getUserFlowUseCase: GetUserFlowUseCase,
    private val addUserUseCase: AddUserUseCase
) : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private var taskDatabase: TaskDatabase? = null
    private var user: User? = null



    private val _success = MutableLiveData<FirebaseUser>()
    val success: LiveData<FirebaseUser>
        get() = _success
    init {

        auth.addAuthStateListener {
            //val userDb = taskDatabase?.taskDatabaseDao()?.getUser(it.currentUser?.email ?: "")
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



    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {

        }.addOnFailureListener {
            _error.value = it.message
        }
//        if (password == user?.password) {
//            MyDatabaseConnection.userId = user?.id
//        }
    }


    fun logout() {
        auth.signOut()
    }
}