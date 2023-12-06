package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.TaskRepository
import com.example.taskscheduler.domain.models.User
import kotlinx.coroutines.flow.Flow

class GetUserFlowUseCase(
    private val repository: TaskRepository
) {

    fun execute(userId: String): Flow<User> {
        return repository.getUser(userId)
    }
}