package com.example.taskscheduler.domain

import android.net.Uri
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow


interface TaskRepository {

    fun getBoards(user: User)

    fun getBoardsFlow(): Flow<List<Board>>

    fun getUser(userId: String): Flow<User>

    suspend fun getUsersForInvites(currentUser: User, board: Board, scope: CoroutineScope)

    fun getBoard()

    fun addUser(user: User)

    fun createNewBoard(
        name: String,
        user: User,
        urlBackground: String,
        board: Board,
        scope: CoroutineScope
    )

    fun updateBoard(board: Board, listOfNotesItemId: String, scope: CoroutineScope): Flow<String>

    fun deleteBoard(board: Board, user: User)

    fun renameList(listOfNotesItem: ListOfNotesItem, board: Board, title: String)

    fun deleteList(listOfNotesItem: ListOfNotesItem, board: Board, isList: Boolean)

    fun createNewList(title: String, board: Board, user: User): LiveData<Board>

    fun getNotesFlow(listOfNotesItemId: String): Flow<List<Note>>

    fun createNewNote(
        title: String,
        description: String,
        board: Board,
        listOfNotesItem: ListOfNotesItem,
        user: User,
        scope: CoroutineScope,
        checkList: List<CheckNoteItem> = emptyList()
    ): LiveData<Board>

    fun updateNote(note: Note): LiveData<List<CheckNoteItem>>

    fun deleteNote(note: Note, board: Board, listOfNotesItem: ListOfNotesItem)

    fun moveNote(listOfNotesItem: ListOfNotesItem, note: Note, board: Board, user: User)

    fun getListOfListNotes(boardId: String): LiveData<List<ListOfNotesItem>>

    fun addBoard(board: Board)

    fun getListsOfNotesFlow(board: Board): Flow<List<ListOfNotesItem>>

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

    fun addUserForInvites(user: User)

    fun addListOfNote(listOfNotesItem: ListOfNotesItem)

    fun addNote(note: Note)

    fun addInvite(invite: Invite)
}