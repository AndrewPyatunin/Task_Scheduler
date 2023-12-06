package com.example.taskscheduler.data

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

    override fun getBoards(): List<BoardDb> {
        return taskDatabaseDao.getBoards()
    }

    override fun getListsOfNotesFlow(boardId: String): Flow<List<ListOfNotesDb>> {
        return taskDatabaseDao.getListsOfNotesFlow()
    }

    override fun getListsOfNotes(): List<ListOfNotesDb> {
        return taskDatabaseDao.getListsOfNotes()
    }

    override fun getNotesFlow(listOfNotesId: String): Flow<List<NoteDb>> {
        return taskDatabaseDao.getNotesFlow()
    }

    override fun getNotes(): List<NoteDb> {
        return taskDatabaseDao.getNotes()
    }

    override fun getInvites(): Flow<List<InviteDb>> {
        return taskDatabaseDao.getInvitesFlow()
    }

    override fun getUsersFlow(): Flow<List<>>

    override fun getUsersForInvites(): Flow<List<UserForInvitesDb>> {
        return taskDatabaseDao.getUsersForInvites()
    }

    override fun addBoard(boardDb: BoardDb) {
        taskDatabaseDao.addBoard(boardDb)
    }

    override fun addUser(userDb: UserDb) {
        taskDatabaseDao.addUser(userDb)
    }

    override fun addNote(noteDb: NoteDb) {
        taskDatabaseDao.addNote(noteDb)
    }

    override fun addListOfNotes(listOfNotesDb: ListOfNotesDb) {
        taskDatabaseDao.addListOfNotes(listOfNotesDb)
    }

    override fun addInvite(inviteDb: InviteDb) {
        taskDatabaseDao.addInvite(inviteDb)
    }

    override fun removeBoard(boardDb: BoardDb) {
        taskDatabaseDao.removeBoard(boardDb.id)
    }

    override fun removeNote(noteDb: NoteDb) {
        taskDatabaseDao.removeNote(noteDb.id)
    }

    override fun removeListOfNotes(listId: String) {
        taskDatabaseDao.removeListOfNotes(listId)
    }

    override fun removeInvite(inviteDb: InviteDb) {
        taskDatabaseDao.removeInvite(inviteDb.id)
    }

    override fun addUserForInvites(userForInvitesDb: UserForInvitesDb) {
        taskDatabaseDao.addUserForInvites(userForInvitesDb)
    }
}