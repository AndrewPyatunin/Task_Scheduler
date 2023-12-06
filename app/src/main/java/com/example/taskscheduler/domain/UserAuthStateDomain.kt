package com.example.taskscheduler.domain


sealed class UserAuthStateDomain {
    object Loading : UserAuthStateDomain()
    data class Success(val user: User) : UserAuthStateDomain()
    data class Error(val message: String) : UserAuthStateDomain()
}