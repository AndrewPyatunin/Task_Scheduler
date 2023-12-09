package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.TaskRepository
import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.User
import kotlinx.coroutines.flow.Flow

class GetBoardsFlowUseCase(
    private val repository: TaskRepository
) {

    fun execute(user: User): Flow<List<Board>> {
        return repository.getBoardsFlow(user)
    }
}