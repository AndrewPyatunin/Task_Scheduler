package com.example.taskscheduler.presentation.myinvites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.MyApp
import com.example.taskscheduler.MyDatabaseConnection
import com.example.taskscheduler.domain.models.Invite
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.domain.usecases.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MyInvitesViewModel : ViewModel() {

    private val inviteRepository = MyApp.inviteRepository
    private val acceptInviteUseCase = AcceptInviteUseCase(inviteRepository)
    private val declineInviteUseCase = DeclineInviteUseCase(inviteRepository)
    private val getInviteUseCase = GetInvitesUseCase(inviteRepository)
    private val fetchInvitesUseCase = FetchInvitesUseCase(inviteRepository)
    private val userRepository = MyApp.userRepository
    private val getUserFromRoomUseCase = GetUserFromRoomUseCase(userRepository)
    val auth = Firebase.auth

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _invitesReady = MutableLiveData<Unit>()
    val invitesReady: LiveData<Unit> = _invitesReady

    private val _invitesListData = MutableLiveData<List<Invite>>()
    val invitesLiveData: LiveData<List<Invite>> = _invitesListData

    init {
        getUserInfo()
        fetchInvites()
    }

    fun getInvitesFromRoom() {
        viewModelScope.launch(Dispatchers.IO) {
            getInviteUseCase.execute().collect {
                _invitesListData.postValue(it)
            }
        }
    }

    fun acceptInvite(user: User, invite: Invite) {
        viewModelScope.launch(Dispatchers.IO) {
            acceptInviteUseCase.execute(user, invite)
        }
    }

    fun declineInvite(user: User, invite: Invite) {
        viewModelScope.launch(Dispatchers.IO) {
            declineInviteUseCase.execute(user, invite)
        }
    }

    private fun fetchInvites() {
        viewModelScope.launch(Dispatchers.IO) {
            _invitesReady.postValue(fetchInvitesUseCase.execute(this))
        }
    }

    private fun getUserInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            _user.postValue(
                getUserFromRoomUseCase.execute(
                    auth.currentUser?.uid ?: MyDatabaseConnection.userId ?: throw RuntimeException("Exception this user is not exist")
                )
            )
        }
    }

    fun logout() {
        auth.signOut()
    }
}