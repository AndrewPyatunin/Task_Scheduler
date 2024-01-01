package com.example.taskscheduler.data.datasources

import com.example.taskscheduler.data.entities.NotesListEntity
import kotlinx.coroutines.flow.Flow

interface NotesListDataSource {

    fun getListsOfNotesFlow(boardId: String): Flow<List<NotesListEntity>>

    suspend fun getListsOfNotes(): List<NotesListEntity>

    suspend fun addListOfNotes(notesListEntity: NotesListEntity)

    suspend fun removeListOfNotes(listId: String)

    suspend fun clearAllNotesListsInRoom()

    suspend fun addAllNotesListItems(notesListEntityList: List<NotesListEntity>)
}