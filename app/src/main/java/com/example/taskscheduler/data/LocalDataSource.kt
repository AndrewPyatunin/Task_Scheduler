package com.example.taskscheduler.data

import com.example.taskscheduler.data.modelsDb.*
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {

    fun getUser(userId: String): Flow<UserDb>

    fun getBoardsFlow(): Flow<List<BoardDb>>

    fun getBoards(): List<BoardDb>

    fun getListsOfNotesFlow(boardId: String): Flow<List<ListOfNotesDb>>

    fun getListsOfNotes(): List<ListOfNotesDb>

    fun getNotesFlow(listOfNotesId: String): Flow<List<NoteDb>>

    fun getNotes(): List<NoteDb>

    fun getInvites(): Flow<List<InviteDb>>

    fun getUsersForInvites(): Flow<List<UserForInvitesDb>>

    fun addBoard(boardDb: BoardDb)

    fun addUser(userDb: UserDb)

    fun addNote(noteDb: NoteDb)

    fun addListOfNotes(listOfNotesDb: ListOfNotesDb)

    fun addInvite(inviteDb: InviteDb)

    fun removeBoard(boardDb: BoardDb)

    fun removeNote(noteDb: NoteDb)

    fun removeListOfNotes(listId: String)

    fun removeInvite(inviteDb: InviteDb)

    fun addUserForInvites(userForInvitesDb: UserForInvitesDb)
}