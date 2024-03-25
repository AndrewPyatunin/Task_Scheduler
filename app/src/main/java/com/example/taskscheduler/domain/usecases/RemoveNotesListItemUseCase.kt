package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.NotesListItem
import com.example.taskscheduler.domain.repos.BoardRepository
import javax.inject.Inject

class RemoveNotesListItemUseCase @Inject constructor(
    private val repository: BoardRepository
) {

    suspend fun execute(notesListItem: NotesListItem, board: Board, isList: Boolean) {
        repository.deleteList(notesListItem, board, isList)
    }
}