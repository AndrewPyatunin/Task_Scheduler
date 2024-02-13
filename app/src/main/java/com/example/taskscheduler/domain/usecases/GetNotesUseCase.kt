package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.repos.NoteRepository

class GetNotesUseCase(
    private val repository: NoteRepository
) {

    fun execute() = repository.getNotesFlow()
}