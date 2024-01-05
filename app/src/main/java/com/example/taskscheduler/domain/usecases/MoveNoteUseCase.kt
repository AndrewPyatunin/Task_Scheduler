package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.Note
import com.example.taskscheduler.domain.models.NotesListItem
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.domain.repos.NoteRepository

class MoveNoteUseCase(
    private val repository: NoteRepository
) {

    suspend fun execute(fromNotesListItem: NotesListItem, notesListItem: NotesListItem, note: Note, board: Board, user: User) {
        repository.moveNote(fromNotesListItem, notesListItem, note, board, user)
    }
}