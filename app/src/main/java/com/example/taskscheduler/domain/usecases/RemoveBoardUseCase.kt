package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.domain.repos.BoardRepository
import javax.inject.Inject

class RemoveBoardUseCase @Inject constructor(
    private val repository: BoardRepository
) {

    suspend fun execute(board: Board, user: User) {
        repository.deleteBoard(board, user)
    }
}