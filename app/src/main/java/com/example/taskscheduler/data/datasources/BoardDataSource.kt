package com.example.taskscheduler.data.datasources

import com.example.taskscheduler.data.entities.BoardEntity
import kotlinx.coroutines.flow.Flow

interface BoardDataSource {

    fun getBoardsFlow(): Flow<List<BoardEntity>>

    suspend fun addBoard(boardEntity: BoardEntity)

    suspend fun addBoards(boardList: List<BoardEntity>)

    suspend fun removeBoard(boardEntity: BoardEntity)

    suspend fun getBoard(boardId: String): BoardEntity

    suspend fun clearAllBoardsInRoom()
}