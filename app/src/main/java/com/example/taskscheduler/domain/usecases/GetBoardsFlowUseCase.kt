package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.domain.repos.BoardRepository
import kotlinx.coroutines.flow.Flow

class GetBoardsFlowUseCase(
    private val repository: BoardRepository
) {

    fun execute(user: User): Flow<List<Board>> {
        return repository.getBoardsFlow(user)
    }
}