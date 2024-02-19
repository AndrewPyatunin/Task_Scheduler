package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.NotesListItem
import com.example.taskscheduler.domain.repos.NotesListRepository
import javax.inject.Inject

class RenameListUseCase @Inject constructor(
    private val repository: NotesListRepository
) {

    suspend fun execute(notesListItem: NotesListItem, board: Board, title: String) {
        repository.renameList(notesListItem, board, title)
    }
}