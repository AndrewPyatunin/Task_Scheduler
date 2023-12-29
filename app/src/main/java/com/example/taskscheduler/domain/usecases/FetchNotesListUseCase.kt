package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.repos.NotesListRepository
import kotlinx.coroutines.CoroutineScope

class FetchNotesListUseCase(
    private val notesListRepository: NotesListRepository
) {

    suspend fun execute(boardId: String, scope: CoroutineScope) {
        notesListRepository.fetchNotesLists(boardId, scope)
    }
}