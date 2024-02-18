package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.repos.UserAuth
import kotlinx.coroutines.CoroutineScope

class GetUserUseCase(
    private val repository: UserAuth
) {

    suspend fun execute(userId: String, scope: CoroutineScope) = repository.getUser(userId, scope)
}