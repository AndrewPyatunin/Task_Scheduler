package com.example.taskscheduler.data.datasources

import com.example.taskscheduler.data.TaskDatabaseDao
import com.example.taskscheduler.data.entities.NotesListEntity
import kotlinx.coroutines.flow.Flow

class NotesListDataSourceImpl(
    private val taskDatabaseDao: TaskDatabaseDao
) : NotesListDataSource {

    override fun getListsOfNotesFlow(boardId: String): Flow<List<NotesListEntity>> {
        return taskDatabaseDao.getListsOfNotesFlow()
    }

    override suspend fun getListsOfNotes(): List<NotesListEntity> {
        return taskDatabaseDao.getListsOfNotes()
    }

    override suspend fun addListOfNotes(notesListEntity: NotesListEntity) {
        taskDatabaseDao.addListOfNotes(notesListEntity)
    }

    override suspend fun removeListOfNotes(listId: String) {
        taskDatabaseDao.removeListOfNotes(listId)
    }

    override suspend fun clearAllNotesListsInRoom() {
        taskDatabaseDao.clearAllListsOfNotes()
    }

    override suspend fun addAllNotesListItems(notesListEntityList: List<NotesListEntity>) {
        taskDatabaseDao.addAllListsOfNotes(notesListEntityList)
    }
}