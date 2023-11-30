package com.example.taskscheduler.domain

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext


interface TaskRepository {

    fun getBoards(user: User)

    fun getBoardsFlow(): Flow<List<Board>>

    suspend fun getUser(userId: String, scope: CoroutineScope)

    suspend fun getUsersForInvites(currentUser: User, board: Board, scope: CoroutineScope)

    fun getBoard()

    fun createNewBoard(name: String, user: User, urlBackground: String, board: Board)

    fun updateBoard(board: Board, listOfNotesItemId: String)

    fun deleteBoard(board: Board, user: User)

    fun renameList(listOfNotesItem: ListOfNotesItem, board: Board, title: String)

    fun deleteList(listOfNotesItem: ListOfNotesItem, board: Board, isList: Boolean)

    fun createNewList(title: String, board: Board, user: User): LiveData<Board>

    fun getNotes(listNotesIds: List<String>): LiveData<List<Note>>

    fun createNewNote(
        title: String,
        description: String,
        board: Board,
        listOfNotesItem: ListOfNotesItem,
        user: User,
        scope: CoroutineScope,
        checkList: List<CheckNoteItem> = emptyList()
    ): LiveData<Board>

    fun updateNote(note: Note): LiveData<List<CheckNoteItem>>

    fun deleteNote(note: Note, board: Board, listOfNotesItem: ListOfNotesItem)

    fun moveNote(listOfNotesItem: ListOfNotesItem, note: Note, board: Board, user: User)

    fun getListOfListNotes(boardId: String): LiveData<List<ListOfNotesItem>>

    fun addBoard(board: Board)
}