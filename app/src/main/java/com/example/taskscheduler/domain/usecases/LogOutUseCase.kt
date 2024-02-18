package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.repos.UserAuth
import com.example.taskscheduler.domain.models.User
import kotlinx.coroutines.CoroutineScope

class LogOutUseCase(
    private val repository: UserAuth
) {

    fun execute(user: User, scope: CoroutineScope) {
        repository.logout(user, scope)
    }
}