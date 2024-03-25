package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.repos.NoteRepository
import javax.inject.Inject

class GetNotesUseCase @Inject constructor(
    private val repository: NoteRepository
) {

    fun execute() = repository.getNotesFlow()
}