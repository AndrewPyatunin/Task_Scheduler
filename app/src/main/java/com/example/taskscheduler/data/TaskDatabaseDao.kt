package com.example.taskscheduler.data

import androidx.room.*
import com.example.taskscheduler.data.entities.*
import com.example.taskscheduler.domain.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDatabaseDao {

    @Query("SELECT * FROM boards")
    fun getBoardsFlow(): Flow<List<BoardEntity>>

    @Query("SELECT * FROM notes")
    fun getNotesFlow(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM listsOfNotes")
    fun getListsOfNotesFlow(): Flow<List<NotesListEntity>>

    @Query("SELECT * FROM invites")
    fun getInvitesFlow(): Flow<List<InviteEntity>>

    @Query("SELECT * FROM users")
    fun getUsersFlow(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNote(note: NoteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNotes(notes: List<NoteEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBoard(board: BoardEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBoardList(boards: List<BoardEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addInvite(invite: InviteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addInvites(inviteList: List<InviteEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllUsers(users: List<UserEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addListOfNotes(listOfNotesItem: NotesListEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllListsOfNotes(list: List<NotesListEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(user: UserEntity)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun removeNote(id: String)

    @Query("DELETE FROM boards WHERE id = :id")
    suspend fun removeBoard(id: String)

    @Query("DELETE FROM listsOfNotes WHERE id = :id")
    suspend fun removeListOfNotes(id: String)

    @Query("DELETE FROM invites WHERE id = :id")
    suspend fun removeInvite(id: String)

    @Query("DELETE FROM invites")
    suspend fun clearAllInvites()

    @Query("DELETE FROM boards")
    suspend fun clearAllBoards()

    @Query("DELETE FROM listsOfNotes")
    suspend fun clearAllListsOfNotes()

    @Query("DELETE FROM notes")
    suspend fun clearAllNotes()

    @Query("DELETE FROM users")
    suspend fun clearAllUsers()

    @Query("SELECT * FROM notes")
    suspend fun getNotes(): List<NoteEntity>

    @Query("SELECT * FROM listsOfNotes")
    suspend fun getListsOfNotes(): List<NotesListEntity>

    @Query("SELECT * FROM boards WHERE id = :id")
    suspend fun getBoard(id: String): BoardEntity

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUser(id: String): UserEntity
}