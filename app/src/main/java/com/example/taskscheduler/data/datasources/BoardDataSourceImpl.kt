package com.example.taskscheduler.data.datasources

import com.example.taskscheduler.data.TaskDatabaseDao
import com.example.taskscheduler.data.entities.BoardEntity
import kotlinx.coroutines.flow.Flow

class BoardDataSourceImpl(
    private val taskDatabaseDao: TaskDatabaseDao
) : BoardDataSource
{

    override fun getBoardsFlow(): Flow<List<BoardEntity>> {
        return taskDatabaseDao.getBoardsFlow()
    }

    override suspend fun getBoards(): List<BoardEntity> {
        return taskDatabaseDao.getBoards()
    }

    override suspend fun addBoard(boardEntity: BoardEntity) {
        taskDatabaseDao.addBoard(boardEntity)
    }

    override suspend fun removeBoard(boardEntity: BoardEntity) {
        taskDatabaseDao.removeBoard(boardEntity.id)
    }
}