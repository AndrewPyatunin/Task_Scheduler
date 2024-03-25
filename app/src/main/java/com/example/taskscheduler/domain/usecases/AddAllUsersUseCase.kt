package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.repos.UserRepository
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class AddAllUsersUseCase @Inject constructor(
    private val repository: UserRepository
) {

    suspend fun execute(scope: CoroutineScope) = repository.addAllUsers(scope)
}