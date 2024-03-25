package com.example.taskscheduler.data.datasources

import com.example.taskscheduler.data.database.NoteDao
import com.example.taskscheduler.data.entities.NoteEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NoteDataSourceImpl @Inject constructor(
    private val noteDao: NoteDao
) : NoteDataSource {

    override fun getNotesFlow(): Flow<List<NoteEntity>> {
        return noteDao.getNotesFlow()
    }

    override suspend fun addNote(noteEntity: NoteEntity) {
        noteDao.addNote(noteEntity)
    }

    override suspend fun addNotes(notes: List<NoteEntity>) {
        noteDao.addNotes(notes)
    }

    override suspend fun removeNote(noteEntity: NoteEntity) {
        noteDao.removeNote(noteEntity.id)
    }

    override fun getNote(noteId: String): Flow<NoteEntity> {
        return noteDao.getNote(noteId)
    }

    override suspend fun getNotes(): List<NoteEntity> {
        return noteDao.getNotes()
    }

    override suspend fun clearAllNotesInRoom() {
        noteDao.clearAllNotes()
    }


}