package com.example.taskscheduler.data

import androidx.room.*
import com.example.taskscheduler.data.modelsDb.*
import com.example.taskscheduler.domain.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDatabaseDao {

    @Query("SELECT * FROM boards")
    fun getBoardsFlow(): Flow<List<BoardDb>>

    @Query("SELECT * FROM boards")
    suspend fun getBoards(): List<BoardDb>

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
    suspend fun addNote(note: NoteDb)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBoard(board: BoardDb)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addInvite(invite: InviteDb)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addListOfNotes(listOfNotesItem: ListOfNotesDb)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(user: UserDb)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun removeNote(id: String)

    @Query("DELETE FROM boards WHERE id = :id")
    suspend fun removeBoard(id: String)

    @Query("DELETE FROM listsOfNotes WHERE id = :id")
    suspend fun removeListOfNotes(id: String)

    @Query("DELETE FROM invites WHERE id = :id")
    suspend fun removeInvite(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUserForInvites(userForInvitesDb: UserForInvitesDb)

    @Query("SELECT * FROM notes")
    suspend fun getNotes(): List<NoteDb>

    @Query("SELECT * FROM listsOfNotes")
    suspend fun getListsOfNotes(): List<ListOfNotesDb>

}