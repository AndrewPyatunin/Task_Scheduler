package com.example.taskscheduler.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.taskscheduler.data.entities.BoardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BoardDao {

    @Query("SELECT * FROM boards")
    fun getBoardsFlow(): Flow<List<BoardEntity>>

    @Query("SELECT * FROM boards WHERE id = :id")
    fun getBoard(id: String): Flow<BoardEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBoard(board: BoardEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBoardList(boards: List<BoardEntity>)

    @Query("DELETE FROM boards WHERE id = :id")
    suspend fun removeBoard(id: String)

    @Query("DELETE FROM boards")
    suspend fun clearAllBoards()
}