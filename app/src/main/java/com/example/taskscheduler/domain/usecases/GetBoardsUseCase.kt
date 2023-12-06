package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.TaskRepository
import com.example.taskscheduler.domain.models.User

class GetBoardsUseCase(
    private val repository: TaskRepository
) {
    fun execute(user: User) {
        repository.getBoards(user)
    }
}