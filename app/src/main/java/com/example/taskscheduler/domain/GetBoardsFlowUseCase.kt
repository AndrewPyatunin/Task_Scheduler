package com.example.taskscheduler.domain

import kotlinx.coroutines.flow.Flow

class GetBoardsFlowUseCase(
    private val repository: TaskRepository
) {

    fun execute(user: User): Flow<List<Board>> {
        repository.getBoards(user)
        return repository.getBoardsFlow()
    }
}