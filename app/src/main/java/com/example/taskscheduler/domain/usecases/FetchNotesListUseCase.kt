package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.models.NotesListItem
import com.example.taskscheduler.domain.repos.NotesListRepository
import kotlinx.coroutines.CoroutineScope

class FetchNotesListUseCase(
    private val notesListRepository: NotesListRepository
) {

    suspend fun execute(boardId: String, scope: CoroutineScope, listOfLists: List<NotesListItem>) {
        notesListRepository.fetchNotesLists(boardId, scope, listOfLists)
    }
}