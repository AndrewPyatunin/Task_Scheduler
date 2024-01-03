package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.domain.repos.BoardRepository
import kotlinx.coroutines.CoroutineScope

class FetchBoardsUseCase(
    private val boardRepository: BoardRepository
) {

    suspend fun execute(user: User, scope: CoroutineScope, boardList: List<Board>) {
        boardRepository.getBoardsFlow(user, scope, boardList)
    }

}