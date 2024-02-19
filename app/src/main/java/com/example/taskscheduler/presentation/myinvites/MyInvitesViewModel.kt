package com.example.taskscheduler.presentation.myinvites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.MyDatabaseConnection
import com.example.taskscheduler.domain.models.Invite
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.domain.usecases.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class MyInvitesViewModel @Inject constructor(
    private val acceptInviteUseCase: AcceptInviteUseCase,
    private val declineInviteUseCase: DeclineInviteUseCase,
    private val getInviteUseCase: GetInvitesUseCase,
    private val fetchInvitesUseCase: FetchInvitesUseCase,
    private val getUserFromRoomUseCase: GetUserFromRoomUseCase
) : ViewModel() {

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
                    auth.currentUser?.uid ?: MyDatabaseConnection.userId
                    ?: throw RuntimeException("Exception this user is not exist")
                )
            )
        }
    }
}