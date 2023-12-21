package com.example.taskscheduler.domain.repos

import androidx.lifecycle.LiveData
import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.NotesListItem
import com.example.taskscheduler.domain.models.User
import kotlinx.coroutines.flow.Flow

interface NotesListRepository {

    fun getNotesLists(boardId: String): LiveData<List<NotesListItem>>

    fun getNotesListsFlow(board: Board): Flow<List<NotesListItem>>

    suspend fun renameList(notesListItem: NotesListItem, board: Board, title: String)

    suspend fun deleteList(notesListItem: NotesListItem, board: Board, isList: Boolean)

    suspend fun createNewList(title: String, board: Board, user: User): Board

    suspend fun addListOfNote(notesListItem: NotesListItem)

}