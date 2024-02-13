package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.NotesListItem
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.domain.repos.NoteRepository

class AddNoteUseCase(
    private val repository: NoteRepository
) {

    suspend fun execute(
        board: Board,
        description: String,
        notesListItem: NotesListItem,
        title: String,
        user: User
    ) = repository.createNewNote(title, description, board, notesListItem, user)
}