package com.example.taskscheduler.presentation

import com.example.taskscheduler.domain.User

sealed class UserAuthState {
    object Loading : UserAuthState()
    data class Success(val user: User) : UserAuthState()
    data class Error(val message: String) : UserAuthState()
}