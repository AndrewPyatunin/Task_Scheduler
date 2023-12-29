package com.example.taskscheduler.domain.repos

import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.NotesListItem
import com.example.taskscheduler.domain.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface BoardRepository {

    suspend fun getBoardsFlow(user: User, scope: CoroutineScope, boardList: List<Board>)

    fun getBoardsFlowFromRoom(user: User): Flow<List<Board>>

    suspend fun updateBoard(board: Board, notesListItem: NotesListItem)

    suspend fun deleteBoard(board: Board, user: User)

    suspend fun addBoard(board: Board)

    suspend fun createNewBoard(
        name: String,
        user: User,
        urlBackground: String,
        board: Board
    ): Result<String>

    suspend fun getBoard(boardId: String): Board

    suspend fun deleteList(notesListItem: NotesListItem, board: Board, isList: Boolean)

    suspend fun clearAllBoards()
}