package com.example.taskscheduler.data.repos

import com.example.taskscheduler.MyDatabaseConnection
import com.example.taskscheduler.data.FirebaseConstants.NOTES
import com.example.taskscheduler.data.FirebaseConstants.NOTES_LIST
import com.example.taskscheduler.data.FirebaseConstants.PATH_NOTES_LIST
import com.example.taskscheduler.data.database.NoteDao
import com.example.taskscheduler.data.database.NotesListDao
import com.example.taskscheduler.data.datasources.NoteDataSourceImpl
import com.example.taskscheduler.data.datasources.NotesListDataSourceImpl
import com.example.taskscheduler.data.mappers.*
import com.example.taskscheduler.domain.CheckNoteItem
import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.Note
import com.example.taskscheduler.domain.models.NotesListItem
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.domain.repos.NoteRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteRepositoryImpl(
    noteDao: NoteDao,
    notesListDao: NotesListDao
) : NoteRepository {

    private val database: FirebaseDatabase = Firebase.database
    private val noteDataSource = NoteDataSourceImpl(noteDao)
    private val notesListDataSource = NotesListDataSourceImpl(notesListDao)
    private val noteToNoteEntityMapper =
        NoteToNoteEntityMapper(CheckNoteItemToCheckNoteEntityMapper())
    private val noteEntityToNoteMapper =
        NoteEntityToNoteMapper(CheckNoteEntityToCheckNoteItemMapper())
    private val notesListItemToNotesListEntityMapper = NotesListItemToNotesListEntityMapper()
    private val databaseNotesListsReference = database.getReference(NOTES_LIST)
    private val databaseNotesReference = database.getReference(NOTES)


    private suspend fun addListOfNote(notesListItem: NotesListItem) {
        notesListDataSource.addListOfNotes(notesListItemToNotesListEntityMapper.map(notesListItem))
    }

    override fun getNotesFlow(): Flow<List<Note>> {
        return noteDataSource.getNotesFlow().map {
            it.map { noteEntity ->
                noteEntityToNoteMapper.map(noteEntity)
            }
        }
    }

    override suspend fun fetchNotes(
        notesListItem: NotesListItem,
        listNotes: List<Note>,
        scope: CoroutineScope
    ) = suspendCancellableCoroutine { continuation ->
        databaseNotesReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val notes = ArrayList<Note>()
                snapshot.children.forEach {
                    if (it.key in notesListItem.listNotes) {
                        it.getValue(Note::class.java)?.let { note ->
                            if (note !in listNotes)
                                notes.add(note)
                        }
                    }
                }
                scope.launch(Dispatchers.IO) {
                    val result = withContext(Dispatchers.IO) {
                        addNotes(notes)
                    }
                    if (continuation.isActive) continuation.resumeWith(Result.success(result))
                }
            }

            override fun onCancelled(error: DatabaseError) = Unit

        })
    }

    override suspend fun createNewNote(
        title: String,
        description: String,
        board: Board,
        notesListItem: NotesListItem,
        user: User,
        checkList: List<CheckNoteItem>
    ) {
        val childListNotesRef = databaseNotesListsReference
            .child(board.id).child(notesListItem.id).child(PATH_NOTES_LIST)
        val url = childListNotesRef.push()
        val idNote = url.key ?: ""

        val note = Note(idNote, title, user.id, emptyList(), description, "", checkList)
        databaseNotesReference.child(idNote).setValue(note)
        addNote(note)
        addListOfNote(notesListItem.copy(listNotes = (notesListItem.listNotes as MutableMap).apply {
            this[idNote] = true
        }))
        url.setValue(true)
        MyDatabaseConnection.updated = true
    }

    override suspend fun updateNote(note: Note) {
        databaseNotesReference.child(note.id).setValue(note)
        addNote(note)
    }

    override suspend fun deleteNote(note: Note, board: Board, notesListItem: NotesListItem) {
        MyDatabaseConnection.updated = true
        databaseNotesReference.child(note.id).removeValue()
        notesListItem.listNotes.filter {
            it.key == note.id
        }.let {
            addListOfNote(notesListItem.copy(listNotes = it))
            databaseNotesListsReference.child(board.id).child(notesListItem.id)
                .child(NOTES_LIST).setValue(it)
        }
        noteDataSource.removeNote(noteToNoteEntityMapper.map(note))
    }

    override suspend fun moveNote(
        fromNotesListItem: NotesListItem,
        notesListItem: NotesListItem,
        note: Note,
        board: Board,
        user: User
    ) {
        MyDatabaseConnection.updated = true
        deleteNote(note, board, fromNotesListItem)
        createNewNote(note.title, note.description, board, notesListItem, user, note.listOfTasks)
    }

    override suspend fun addNote(note: Note) {
        noteDataSource.addNote(noteToNoteEntityMapper.map(note))
    }

    override suspend fun addNotes(notes: List<Note>) {
        noteDataSource.addNotes(notes.map {
            noteToNoteEntityMapper.map(it)
        })
    }

    override suspend fun clearAllNotes() {
        noteDataSource.clearAllNotesInRoom()
    }

}