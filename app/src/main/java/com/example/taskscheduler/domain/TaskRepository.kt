package com.example.taskscheduler.domain

import android.net.Uri
import androidx.lifecycle.LiveData
import com.example.taskscheduler.domain.models.*
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow


interface TaskRepository {

    fun getBoardsFlow(user: User): Flow<List<Board>>

    fun getBoardsFlowFromRoom(): Flow<List<Board>>

    fun getUser(userId: String): Flow<User>

    suspend fun getUsersForInvites(currentUser: User, board: Board, scope: CoroutineScope)

    fun getBoard()

    suspend fun addUser(user: User)

    fun createNewBoard(
        name: String,
        user: User,
        urlBackground: String,
        board: Board
    ): Flow<Board>

    fun updateBoard(board: Board, listOfNotesItemId: String, scope: CoroutineScope): Flow<String>

    fun deleteBoard(board: Board, user: User)

    fun renameList(notesListItem: NotesListItem, board: Board, title: String)

    fun deleteList(notesListItem: NotesListItem, board: Board, isList: Boolean)

    fun createNewList(title: String, board: Board, user: User): LiveData<Board>

    fun getNotesFlow(listOfNotesItemId: String): Flow<List<Note>>

    fun createNewNote(
        title: String,
        description: String,
        board: Board,
        notesListItem: NotesListItem,
        user: User,
        scope: CoroutineScope,
        checkList: List<CheckNoteItem> = emptyList()
    ): LiveData<Board>

    fun updateNote(note: Note): LiveData<List<CheckNoteItem>>

    fun deleteNote(note: Note, board: Board, notesListItem: NotesListItem)

    fun moveNote(notesListItem: NotesListItem, note: Note, board: Board, user: User)

    fun getListOfListNotes(boardId: String): LiveData<List<NotesListItem>>

    suspend fun addBoard(board: Board)

    fun getListsOfNotesFlow(board: Board): Flow<List<NotesListItem>>

    fun getInvitesFlow(): Flow<List<Invite>>

    fun getUserFlow(userId: String): Flow<User>

    fun getUsersForInvitesFlow(): Flow<List<User>>

    fun signUp(
        email: String,
        password: String,
        name: String,
        lastName: String,
        uri: Uri?
    ): LiveData<User>

    fun updateUserAvatar(uri: Uri, name: String): LiveData<FirebaseUser>

    fun uploadUserAvatar(uri: Uri, name: String, callback: UrlCallback)

    fun updateUserProfile(description: String, email: String): LiveData<String>

    fun update(uri: Uri?, name: String)

    fun updateUserEmail(email: String, ref: DatabaseReference): LiveData<String>

    fun updateStatus()

    fun getInvites(): LiveData<List<Invite>>

    fun acceptInvite(user: User, invite: Invite)

    fun declineInvite(user: User, invite: Invite)

    fun clearInviteInDatabase(userId: String, inviteBoardId: String)

    fun inviteUser(userForInvite: User, currentUser: User, board: Board): LiveData<String>

    fun logout()


    interface UrlCallback {
        fun onUrlCallback(url: String)
    }

    suspend fun addUserForInvites(user: User)

    suspend fun addListOfNote(notesListItem: NotesListItem)

    suspend fun addNote(note: Note)

    suspend fun addInvite(invite: Invite)
}