package com.example.taskscheduler.domain

import com.example.taskscheduler.domain.models.User


sealed class UserAuthStateDomain {
    object Loading : UserAuthStateDomain()
    data class Success(val user: User) : UserAuthStateDomain()
    data class Error(val message: String) : UserAuthStateDomain()
}