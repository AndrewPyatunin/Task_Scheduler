package com.example.taskscheduler.data

import androidx.room.*
import com.example.taskscheduler.domain.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDatabaseDao {

    @Query("SELECT * FROM boards")
    fun getBoardsFlow(): Flow<List<BoardDb>>

    @Query("SELECT * FROM boards")
    fun getBoards(): List<BoardDb>

    @Query("SELECT * FROM notes")
    fun getNotesFlow(): Flow<List<NoteDb>>

    @Query("SELECT * FROM listsOfNotes")
    fun getListsOfNotesFlow(): Flow<List<ListOfNotesDb>>

    @Query("SELECT * FROM invites")
    fun getInvitesFlow(): Flow<List<InviteDb>>

//    @Query("SELECT * FROM users WHERE email = :email")
//    fun getUser(email: String): User

    @Query("SELECT * FROM users WHERE id = :id")
    fun getUser(id: String): Flow<UserDb>

    @Query("SELECT * FROM users")
    fun getUsersFlow(): Flow<UserDb>

    @Query("SELECT * FROM usersForInvites")
    fun getUsersForInvites(): Flow<List<UserForInvitesDb>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addNote(note: NoteDb)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addBoard(board: BoardDb)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addInvite(invite: InviteDb)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addListOfNotes(listOfNotesItem: ListOfNotesDb)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addUser(user: UserDb)

    @Query("DELETE FROM notes WHERE id = :id")
    fun removeNote(id: String)

    @Query("DELETE FROM boards WHERE id = :id")
    fun removeBoard(id: String)

    @Query("DELETE FROM listsOfNotes WHERE id = :id")
    fun removeListOfNotes(id: String)

    @Query("DELETE FROM invites WHERE id = :id")
    fun removeInvite(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addUserForInvites(userForInvitesDb: UserForInvitesDb)

    @Query("SELECT * FROM notes")
    fun getNotes(): List<NoteDb>

    @Query("SELECT * FROM listsOfNotes")
    fun getListsOfNotes(): List<ListOfNotesDb>

}