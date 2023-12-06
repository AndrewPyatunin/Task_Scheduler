package com.example.taskscheduler.domain

class GetUserFlowFromRoomUseCase(
    private val repository: TaskRepository
) {
    fun execute(userId: String) = repository.getUserFlow(userId)
}