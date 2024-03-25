package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.Note
import com.example.taskscheduler.domain.models.NotesListItem
import com.example.taskscheduler.domain.repos.NoteRepository
import javax.inject.Inject

class RemoveNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {

    suspend fun execute(board: Board, note: Note, notesListItem: NotesListItem) {
        repository.deleteNote(note, board, notesListItem)
    }

}