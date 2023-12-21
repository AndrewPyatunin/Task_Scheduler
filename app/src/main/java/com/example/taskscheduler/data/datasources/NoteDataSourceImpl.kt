package com.example.taskscheduler.data.datasources

import com.example.taskscheduler.data.TaskDatabaseDao
import com.example.taskscheduler.data.entities.NoteEntity
import kotlinx.coroutines.flow.Flow

class NoteDataSourceImpl(
    private val taskDatabaseDao: TaskDatabaseDao
) : NoteDataSource {

    override fun getNotesFlow(listOfNotesId: String): Flow<List<NoteEntity>> {
        return taskDatabaseDao.getNotesFlow()
    }

    override suspend fun addNote(noteEntity: NoteEntity) {
        taskDatabaseDao.addNote(noteEntity)
    }

    override suspend fun removeNote(noteEntity: NoteEntity) {
        taskDatabaseDao.removeNote(noteEntity.id)
    }

    override suspend fun getNotes(): List<NoteEntity> {
        return taskDatabaseDao.getNotes()
    }
}