package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.domain.repos.BoardRepository

class RemoveBoardUseCase(
    private val repository: BoardRepository
) {

    suspend fun execute(board: Board, user: User) {
        repository.deleteBoard(board, user)
    }
}