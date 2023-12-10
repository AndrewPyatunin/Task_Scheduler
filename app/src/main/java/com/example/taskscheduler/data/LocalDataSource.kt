package com.example.taskscheduler.data

import com.example.taskscheduler.data.entities.*
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {

    fun getUser(userId: String): Flow<UserEntity>

    fun getBoardsFlow(): Flow<List<BoardEntity>>

    suspend fun getBoards(): List<BoardEntity>

    fun getListsOfNotesFlow(boardId: String): Flow<List<NotesListEntity>>

    suspend fun getListsOfNotes(): List<NotesListEntity>

    fun getNotesFlow(listOfNotesId: String): Flow<List<NoteEntity>>

    suspend fun getNotes(): List<NoteEntity>

    fun getInvites(): Flow<List<InviteEntity>>

    fun getUsersForInvites(): Flow<List<UserForInvitesEntity>>

    suspend fun addBoard(boardEntity: BoardEntity)

    suspend fun addUser(userEntity: UserEntity)

    suspend fun addNote(noteEntity: NoteEntity)

    suspend fun addListOfNotes(notesListEntity: NotesListEntity)

    suspend fun addInvite(inviteEntity: InviteEntity)

    suspend fun removeBoard(boardEntity: BoardEntity)

    suspend fun removeNote(noteEntity: NoteEntity)

    suspend fun removeListOfNotes(listId: String)

    suspend fun removeInvite(inviteEntity: InviteEntity)

    suspend fun addUserForInvites(userForInvitesEntity: UserForInvitesEntity)
}