package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.models.Note
import com.example.taskscheduler.domain.models.NotesListItem
import com.example.taskscheduler.domain.repos.NoteRepository
import kotlinx.coroutines.CoroutineScope

class FetchNotesUseCase(
    private val repository: NoteRepository
) {

    suspend fun execute(notesListItem: NotesListItem, listNotes: List<Note>, scope: CoroutineScope) {
        repository.fetchNotes(notesListItem, listNotes, scope)
    }
}