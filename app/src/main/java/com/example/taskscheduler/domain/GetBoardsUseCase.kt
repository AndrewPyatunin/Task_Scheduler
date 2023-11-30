package com.example.taskscheduler.domain

class GetBoardsUseCase(
    private val repository: TaskRepository
) {
    fun execute(user: User) {
        repository.getBoards(user)
    }
}