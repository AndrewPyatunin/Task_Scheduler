package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.repos.UserRepository
import kotlinx.coroutines.CoroutineScope

class AddAllUsersUseCase(
    private val repository: UserRepository
) {

    suspend fun execute(scope: CoroutineScope) = repository.addAllUsers(scope)
}