package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.repos.NotesListRepository
import javax.inject.Inject

class GetNotesListsUseCase @Inject constructor(
    private val repository: NotesListRepository
) {

    fun execute(board: Board) = repository.getNotesListsFlow(board)
}