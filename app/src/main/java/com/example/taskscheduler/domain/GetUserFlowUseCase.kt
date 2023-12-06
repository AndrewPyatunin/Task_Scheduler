package com.example.taskscheduler.domain

import kotlinx.coroutines.flow.Flow

class GetUserFlowUseCase(
    private val repository: TaskRepository
) {

    fun execute(userId: String): Flow<User> {
        return repository.getUser(userId)
    }
}