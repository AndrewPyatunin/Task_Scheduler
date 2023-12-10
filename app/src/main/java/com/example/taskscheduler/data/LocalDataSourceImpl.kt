package com.example.taskscheduler.data

import com.example.taskscheduler.data.entities.*
import kotlinx.coroutines.flow.Flow

class LocalDataSourceImpl(
    private val taskDatabaseDao: TaskDatabaseDao
): LocalDataSource {

    override fun getUser(userId: String): Flow<UserEntity> {
        return taskDatabaseDao.getUser(userId)
    }

    override fun getBoardsFlow(): Flow<List<BoardEntity>> {
        return taskDatabaseDao.getBoardsFlow()
    }

    override suspend fun getBoards(): List<BoardEntity> {
        return taskDatabaseDao.getBoards()
    }

    override fun getListsOfNotesFlow(boardId: String): Flow<List<NotesListEntity>> {
        return taskDatabaseDao.getListsOfNotesFlow()
    }

    override suspend fun getListsOfNotes(): List<NotesListEntity> {
        return taskDatabaseDao.getListsOfNotes()
    }

    override fun getNotesFlow(listOfNotesId: String): Flow<List<NoteEntity>> {
        return taskDatabaseDao.getNotesFlow()
    }

    override suspend fun getNotes(): List<NoteEntity> {
        return taskDatabaseDao.getNotes()
    }

    override fun getInvites(): Flow<List<InviteEntity>> {
        return taskDatabaseDao.getInvitesFlow()
    }

    override fun getUsersForInvites(): Flow<List<UserForInvitesEntity>> {
        return taskDatabaseDao.getUsersForInvites()
    }

    override suspend fun addBoard(boardEntity: BoardEntity) {
        taskDatabaseDao.addBoard(boardEntity)
    }

    override suspend fun addUser(userEntity: UserEntity) {
        taskDatabaseDao.addUser(userEntity)
    }

    override suspend fun addNote(noteEntity: NoteEntity) {
        taskDatabaseDao.addNote(noteEntity)
    }

    override suspend fun addListOfNotes(notesListEntity: NotesListEntity) {
        taskDatabaseDao.addListOfNotes(notesListEntity)
    }

    override suspend fun addInvite(inviteEntity: InviteEntity) {
        taskDatabaseDao.addInvite(inviteEntity)
    }

    override suspend fun removeBoard(boardEntity: BoardEntity) {
        taskDatabaseDao.removeBoard(boardEntity.id)
    }

    override suspend fun removeNote(noteEntity: NoteEntity) {
        taskDatabaseDao.removeNote(noteEntity.id)
    }

    override suspend fun removeListOfNotes(listId: String) {
        taskDatabaseDao.removeListOfNotes(listId)
    }

    override suspend fun removeInvite(inviteEntity: InviteEntity) {
        taskDatabaseDao.removeInvite(inviteEntity.id)
    }

    override suspend fun addUserForInvites(userForInvitesEntity: UserForInvitesEntity) {
        taskDatabaseDao.addUserForInvites(userForInvitesEntity)
    }
}