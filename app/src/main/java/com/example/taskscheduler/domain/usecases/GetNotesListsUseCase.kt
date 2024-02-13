package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.repos.NotesListRepository

class GetNotesListsUseCase(
    private val repository: NotesListRepository
) {

    fun execute(board: Board) = repository.getNotesListsFlow(board)
}