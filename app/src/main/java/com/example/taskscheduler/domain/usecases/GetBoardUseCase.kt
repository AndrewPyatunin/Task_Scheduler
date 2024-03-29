package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.repos.BoardRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBoardUseCase @Inject constructor(
    private val repository: BoardRepository
) {

    fun execute(boardId: String): Flow<Board> {
        return repository.getBoard(boardId)
    }
}