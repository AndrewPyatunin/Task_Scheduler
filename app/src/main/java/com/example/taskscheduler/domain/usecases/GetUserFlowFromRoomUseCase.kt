package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.TaskRepository

class GetUserFlowFromRoomUseCase(
    private val repository: TaskRepository
) {
    fun execute(userId: String) = repository.getUserFlow(userId)
}