package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.TaskRepository
import com.example.taskscheduler.domain.models.User

class AddUserUseCase(
    private val repository: TaskRepository
) {

    suspend fun execute(user: User) {
        repository.addUser(user)
    }
}