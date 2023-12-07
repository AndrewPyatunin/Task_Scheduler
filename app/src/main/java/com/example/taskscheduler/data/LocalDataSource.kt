package com.example.taskscheduler.data

import com.example.taskscheduler.data.modelsDb.*
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {

    fun getUser(userId: String): Flow<UserDb>

    fun getBoardsFlow(): Flow<List<BoardDb>>

    suspend fun getBoards(): List<BoardDb>

    fun getListsOfNotesFlow(boardId: String): Flow<List<ListOfNotesDb>>

    suspend fun getListsOfNotes(): List<ListOfNotesDb>

    fun getNotesFlow(listOfNotesId: String): Flow<List<NoteDb>>

    suspend fun getNotes(): List<NoteDb>

    fun getInvites(): Flow<List<InviteDb>>

    fun getUsersForInvites(): Flow<List<UserForInvitesDb>>

    suspend fun addBoard(boardDb: BoardDb)

    suspend fun addUser(userDb: UserDb)

    suspend fun addNote(noteDb: NoteDb)

    suspend fun addListOfNotes(listOfNotesDb: ListOfNotesDb)

    suspend fun addInvite(inviteDb: InviteDb)

    suspend fun removeBoard(boardDb: BoardDb)

    suspend fun removeNote(noteDb: NoteDb)

    suspend fun removeListOfNotes(listId: String)

    suspend fun removeInvite(inviteDb: InviteDb)

    suspend fun addUserForInvites(userForInvitesDb: UserForInvitesDb)
}