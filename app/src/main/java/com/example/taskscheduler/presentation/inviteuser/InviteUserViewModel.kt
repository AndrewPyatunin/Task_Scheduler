package com.example.taskscheduler.presentation.inviteuser

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.MyApp
import com.example.taskscheduler.MyDatabaseConnection
import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.domain.usecases.GetUsersFlowUseCase
import com.example.taskscheduler.domain.usecases.InviteUserUseCase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class InviteUserViewModel : ViewModel() {

    private val inviteUserRepository = MyApp.inviteRepository
    private val userRepository = MyApp.userRepository
    private val inviteUserUseCase: InviteUserUseCase = InviteUserUseCase(inviteUserRepository)
    private val getUsersUseCase = GetUsersFlowUseCase(userRepository)
    private val auth = Firebase.auth

    private val _user = MutableLiveData<FirebaseUser>()
    val user: LiveData<FirebaseUser>
        get() = _user

    private val _listUsers = MutableLiveData<List<User>>()
    val listUsers: LiveData<List<User>>
        get() = _listUsers

    private val _success = MutableLiveData<String>()
    val success: LiveData<String>
        get() = _success

    init {
        auth.addAuthStateListener {
            _user.value = auth.currentUser
        }
    }

    fun getUsersForInvite(board: Board) {
        viewModelScope.launch(Dispatchers.IO) {
            getUsersUseCase.execute().map { list ->
                list.filter {
                    it.id != auth.currentUser?.uid && it.id !in board.members && board.id !in it.invites
                }
            }.collect {
                _listUsers.postValue(it)
            }
        }
    }

    fun inviteUser(userForInvite: User, currentUser: User, board: Board) {
        viewModelScope.launch {
            _success.postValue(inviteUserUseCase.execute(userForInvite, currentUser, board))
        }
    }
}