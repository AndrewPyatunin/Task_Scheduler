package com.example.taskscheduler.data.datasources

import com.example.taskscheduler.data.TaskDatabaseDao
import com.example.taskscheduler.data.entities.NoteEntity
import kotlinx.coroutines.flow.Flow

class NoteDataSourceImpl(
    private val taskDatabaseDao: TaskDatabaseDao
) : NoteDataSource {

    override fun getNotesFlow(): Flow<List<NoteEntity>> {
        return taskDatabaseDao.getNotesFlow()
    }

    override suspend fun addNote(noteEntity: NoteEntity) {
        taskDatabaseDao.addNote(noteEntity)
    }

    override suspend fun addNotes(notes: List<NoteEntity>) {
        taskDatabaseDao.addNotes(notes)
    }

    override suspend fun removeNote(noteEntity: NoteEntity) {
        taskDatabaseDao.removeNote(noteEntity.id)
    }

    override suspend fun getNotes(): List<NoteEntity> {
        return taskDatabaseDao.getNotes()
    }

    override suspend fun clearAllNotesInRoom() {
        taskDatabaseDao.clearAllNotes()
    }


}