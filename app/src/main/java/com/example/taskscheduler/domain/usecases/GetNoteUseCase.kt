package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.models.Note
import com.example.taskscheduler.domain.repos.NoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    fun execute(noteId: String): Flow<Note> {
        return repository.getNote(noteId)
    }
}