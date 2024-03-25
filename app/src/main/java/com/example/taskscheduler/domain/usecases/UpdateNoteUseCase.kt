package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.models.Note
import com.example.taskscheduler.domain.repos.NoteRepository
import javax.inject.Inject

class UpdateNoteUseCase @Inject constructor(
    private val repository: NoteRepository
) {

    suspend fun execute(note: Note) = repository.updateNote(note)
}