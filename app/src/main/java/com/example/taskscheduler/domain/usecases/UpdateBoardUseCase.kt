package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.NotesListItem
import com.example.taskscheduler.domain.repos.BoardRepository

class UpdateBoardUseCase(
    private val repository: BoardRepository
) {

    suspend fun execute(board: Board, notesListItem: NotesListItem) {
        repository.updateBoard(board, notesListItem)
    }

}