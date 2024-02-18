package com.example.taskscheduler.domain.repos

import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.NotesListItem
import com.example.taskscheduler.domain.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface NotesListRepository {

    fun getNotesListsFlow(board: Board): Flow<List<NotesListItem>>

    suspend fun renameList(notesListItem: NotesListItem, board: Board, title: String)

    suspend fun createNewList(title: String, board: Board, user: User): Result<Unit>

    suspend fun addListOfNote(notesListItem: NotesListItem)

    suspend fun addAllListsOfNoteListItem(listNoteList: List<NotesListItem>)

    suspend fun fetchNotesLists(boardId: String, scope: CoroutineScope, list: List<NotesListItem>)

    suspend fun clearAllNotesLists()
}