package com.example.taskscheduler.data.datasources

import com.example.taskscheduler.data.entities.NoteEntity
import kotlinx.coroutines.flow.Flow

interface NoteDataSource {

    fun getNotesFlow(listOfNotesId: String): Flow<List<NoteEntity>>

    suspend fun addNote(noteEntity: NoteEntity)

    suspend fun removeNote(noteEntity: NoteEntity)

    suspend fun getNotes(): List<NoteEntity>
}