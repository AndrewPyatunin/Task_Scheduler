package com.example.taskscheduler.data.repos

import com.example.taskscheduler.MyDatabaseConnection
import com.example.taskscheduler.data.FirebaseConstants.LIST_OF_NOTES
import com.example.taskscheduler.data.FirebaseConstants.NOTES
import com.example.taskscheduler.data.datasources.NoteDataSource
import com.example.taskscheduler.data.entities.NoteEntity
import com.example.taskscheduler.data.mappers.Mapper
import com.example.taskscheduler.domain.CheckNoteItem
import com.example.taskscheduler.domain.repos.NoteRepository
import com.example.taskscheduler.domain.repos.NotesListRepository
import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.Note
import com.example.taskscheduler.domain.models.NotesListItem
import com.example.taskscheduler.domain.models.User
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteRepositoryImpl(
    private val noteDataSource: NoteDataSource,
    private val notesListRepository: NotesListRepository,
    private val noteToNoteEntityMapper: Mapper<Note, NoteEntity>,
    private val noteEntityToNoteMapper: Mapper<NoteEntity, Note>,
    private val database: FirebaseDatabase
) : NoteRepository {

    private val databaseListsOfNotesReference = database.getReference(LIST_OF_NOTES)
    private val databaseNotesReference = database.getReference(NOTES)

    override fun getNotesFlow(listOfNotesItemId: String): Flow<List<Note>> {
        return noteDataSource.getNotesFlow(listOfNotesItemId).map {
            it.map { noteEntity ->
                noteEntityToNoteMapper.map(noteEntity)
            }
        }
    }

    override suspend fun createNewNote(
        title: String,
        description: String,
        board: Board,
        notesListItem: NotesListItem,
        user: User,
        scope: CoroutineScope,
        checkList: List<CheckNoteItem>
    ) {
        val childListNotesRef = databaseListsOfNotesReference
            .child(board.id).child(notesListItem.id).child("listNotes")
        val url = childListNotesRef.push()
        val idNote = url.key ?: ""
        val listNotes = notesListItem.listNotes

        val note = Note(idNote, title, user.id, emptyList(), description, "", checkList)
        databaseNotesReference.child(idNote).setValue(note)
        listNotes as HashMap<String, Boolean>
        listNotes.put(idNote, true)
        addNote(note)
        notesListRepository.addListOfNote(notesListItem.copy(listNotes = listNotes))
        url.setValue(true)
        MyDatabaseConnection.updated = true
    }

    override suspend fun updateNote(note: Note) {
        databaseNotesReference.child(note.id).setValue(note)
        val checkList = ArrayList<CheckNoteItem>()
        checkList.addAll(note.listOfTasks)
        addNote(note)
    }

    override suspend fun deleteNote(note: Note, board: Board, notesListItem: NotesListItem) {
        MyDatabaseConnection.updated = true
        databaseNotesReference.child(note.id).removeValue()
        databaseListsOfNotesReference.child(board.id).child(notesListItem.id)
            .child("listNotes").child(note.id).removeValue()
        noteDataSource.removeNote(noteToNoteEntityMapper.map(note))
        val listNotes: HashMap<String, Boolean> = notesListItem.listNotes as HashMap
        listNotes.remove(note.id)
        notesListRepository.addListOfNote(notesListItem.copy(listNotes = listNotes))
    }

    override suspend fun moveNote(notesListItem: NotesListItem, note: Note, board: Board, user: User) {
        MyDatabaseConnection.updated = true
        deleteNote(note, board, notesListItem)
        createNewNote(note.title, note.description, board, notesListItem, user, CoroutineScope(Dispatchers.IO), note.listOfTasks)
    }

    override suspend fun addNote(note: Note) {
        noteDataSource.addNote(noteToNoteEntityMapper.map(note))
    }
}