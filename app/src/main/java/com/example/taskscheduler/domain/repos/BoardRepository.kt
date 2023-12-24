package com.example.taskscheduler.domain.repos

import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface BoardRepository {

    suspend fun getBoardsFlow(user: User, scope: CoroutineScope)

    fun getBoardsFlowFromRoom(user: User): Flow<List<Board>>


    suspend fun updateBoard(board: Board, listOfNotesItemId: String)

    suspend fun deleteBoard(board: Board, user: User)

    suspend fun addBoard(board: Board)

    suspend fun createNewBoard(
        name: String,
        user: User,
        urlBackground: String,
        board: Board
    )
}