package com.example.taskscheduler.domain

import kotlinx.coroutines.flow.Flow

class GetBoardsFlowUseCase(
    private val repository: TaskRepository
) {

    fun execute(): Flow<List<Board>> {
        repository.getBoards()
        return repository.getBoardsFlow()
    }
}