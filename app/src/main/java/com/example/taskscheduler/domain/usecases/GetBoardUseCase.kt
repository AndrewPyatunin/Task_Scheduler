package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.repos.BoardRepository

class GetBoardUseCase(
    private val repository: BoardRepository
) {

    suspend fun execute(boardId: String): Board {
        return repository.getBoard(boardId)
    }
}