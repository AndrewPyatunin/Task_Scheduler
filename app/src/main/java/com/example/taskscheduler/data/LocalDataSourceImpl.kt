package com.example.taskscheduler.data

import com.example.taskscheduler.data.modelsDb.*
import kotlinx.coroutines.flow.Flow

class LocalDataSourceImpl(
    private val taskDatabaseDao: TaskDatabaseDao
): LocalDataSource {
    override fun getUser(userId: String): Flow<UserDb> {
        return taskDatabaseDao.getUser(userId)
    }

    override fun getBoardsFlow(): Flow<List<BoardDb>> {
        return taskDatabaseDao.getBoardsFlow()
    }

    override suspend fun getBoards(): List<BoardDb> {
        return taskDatabaseDao.getBoards()
    }

    override fun getListsOfNotesFlow(boardId: String): Flow<List<ListOfNotesDb>> {
        return taskDatabaseDao.getListsOfNotesFlow()
    }

    override suspend fun getListsOfNotes(): List<ListOfNotesDb> {
        return taskDatabaseDao.getListsOfNotes()
    }

    override fun getNotesFlow(listOfNotesId: String): Flow<List<NoteDb>> {
        return taskDatabaseDao.getNotesFlow()
    }

    override suspend fun getNotes(): List<NoteDb> {
        return taskDatabaseDao.getNotes()
    }

    override fun getInvites(): Flow<List<InviteDb>> {
        return taskDatabaseDao.getInvitesFlow()
    }

    override fun getUsersForInvites(): Flow<List<UserForInvitesDb>> {
        return taskDatabaseDao.getUsersForInvites()
    }

    override suspend fun addBoard(boardDb: BoardDb) {
        taskDatabaseDao.addBoard(boardDb)
    }

    override suspend fun addUser(userDb: UserDb) {
        taskDatabaseDao.addUser(userDb)
    }

    override suspend fun addNote(noteDb: NoteDb) {
        taskDatabaseDao.addNote(noteDb)
    }

    override suspend fun addListOfNotes(listOfNotesDb: ListOfNotesDb) {
        taskDatabaseDao.addListOfNotes(listOfNotesDb)
    }

    override suspend fun addInvite(inviteDb: InviteDb) {
        taskDatabaseDao.addInvite(inviteDb)
    }

    override suspend fun removeBoard(boardDb: BoardDb) {
        taskDatabaseDao.removeBoard(boardDb.id)
    }

    override suspend fun removeNote(noteDb: NoteDb) {
        taskDatabaseDao.removeNote(noteDb.id)
    }

    override suspend fun removeListOfNotes(listId: String) {
        taskDatabaseDao.removeListOfNotes(listId)
    }

    override suspend fun removeInvite(inviteDb: InviteDb) {
        taskDatabaseDao.removeInvite(inviteDb.id)
    }

    override suspend fun addUserForInvites(userForInvitesDb: UserForInvitesDb) {
        taskDatabaseDao.addUserForInvites(userForInvitesDb)
    }
}