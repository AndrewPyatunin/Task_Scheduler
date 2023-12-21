package com.example.taskscheduler.data.datasources

import com.example.taskscheduler.data.entities.BoardEntity
import kotlinx.coroutines.flow.Flow

interface BoardDataSource {

    fun getBoardsFlow(): Flow<List<BoardEntity>>

    suspend fun getBoards(): List<BoardEntity>

    suspend fun addBoard(boardEntity: BoardEntity)

    suspend fun removeBoard(boardEntity: BoardEntity)

}