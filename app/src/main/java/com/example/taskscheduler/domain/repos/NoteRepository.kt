package com.example.taskscheduler.domain.repos

import com.example.taskscheduler.domain.models.CheckNoteItem
import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.Note
import com.example.taskscheduler.domain.models.NotesListItem
import com.example.taskscheduler.domain.models.User
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    fun getNotesFlow(): Flow<List<Note>>

    fun getNote(noteId: String): Flow<Note>

    suspend fun fetchNotes(notesListItem: NotesListItem, listNotes: List<Note>, scope: CoroutineScope)

    suspend fun createNewNote(
        title: String,
        description: String,
        board: Board,
        notesListItem: NotesListItem,
        user: User,
        onSuccessListener: (String) -> Unit = {},
        date: String = "",
        checkList: List<CheckNoteItem> = emptyList()
    )

    suspend fun updateNote(note: Note)

    suspend fun deleteNote(note: Note, board: Board, notesListItem: NotesListItem)

    suspend fun moveNote(fromNotesListItem: NotesListItem, notesListItem: NotesListItem, note: Note, board: Board, user: User, onSuccessListener: (String) -> Unit)

    suspend fun addNote(note: Note)

    suspend fun addNotes(notes: List<Note>)

    suspend fun clearAllNotes()
}