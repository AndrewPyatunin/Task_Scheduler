package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.domain.repos.BoardRepository

class AddBoardUseCase(
    private val repository: BoardRepository
) {

    suspend fun execute(name: String, user: User, urlBackground: String, board: Board): Result<String> {
        return repository.createNewBoard(name, user, urlBackground, board)
    }
}