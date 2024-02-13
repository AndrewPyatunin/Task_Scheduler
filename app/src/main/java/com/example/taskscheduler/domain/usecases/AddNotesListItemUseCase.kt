package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.domain.repos.NotesListRepository

class AddNotesListItemUseCase(
    private val repository: NotesListRepository
) {

    suspend fun execute(title: String, board: Board, user: User) {
        repository.createNewList(title, board, user)
    }

}