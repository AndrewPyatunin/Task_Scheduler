package com.example.taskscheduler.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.taskscheduler.data.entities.NotesListEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesListDao {

    @Query("SELECT * FROM listsOfNotes")
    fun getListsOfNotesFlow(): Flow<List<NotesListEntity>>

    @Query("SELECT * FROM listsOfNotes")
    suspend fun getListsOfNotes(): List<NotesListEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addListOfNotes(listOfNotesItem: NotesListEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllListsOfNotes(list: List<NotesListEntity>)

    @Query("DELETE FROM listsOfNotes WHERE id = :id")
    suspend fun removeListOfNotes(id: String)

    @Query("DELETE FROM listsOfNotes")
    suspend fun clearAllListsOfNotes()
}