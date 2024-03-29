package com.example.taskscheduler.data.datasources

import com.example.taskscheduler.data.database.BoardDao
import com.example.taskscheduler.data.entities.BoardEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BoardDataSourceImpl @Inject constructor(
    private val taskDatabaseDao: BoardDao
) : BoardDataSource {

    override fun getBoardsFlow(): Flow<List<BoardEntity>> {
        return taskDatabaseDao.getBoardsFlow()
    }

    override fun getBoard(boardId: String): Flow<BoardEntity> {
        return taskDatabaseDao.getBoard(boardId)
    }

    override suspend fun clearAllBoardsInRoom() {
        taskDatabaseDao.clearAllBoards()
    }

    override suspend fun addBoard(boardEntity: BoardEntity) {
        taskDatabaseDao.addBoard(boardEntity)
    }

    override suspend fun addBoards(boardList: List<BoardEntity>) {
        taskDatabaseDao.addBoardList(boardList)
    }

    override suspend fun removeBoard(boardEntity: BoardEntity) {
        taskDatabaseDao.removeBoard(boardEntity.id)
    }
}