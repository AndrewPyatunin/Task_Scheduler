package com.example.taskscheduler.presentation.newnote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.MyDatabaseConnection.updated
import com.example.taskscheduler.domain.*
import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.ListOfNotesItem
import com.example.taskscheduler.domain.models.Note
import com.example.taskscheduler.domain.models.User
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewNoteViewModel: ViewModel() {
    val database = Firebase.database
    private val databaseListsOfNotesRef = database.getReference("ListsOfNotes")
    private val databaseNotesRef = database.getReference("Notes")
    val databaseBoardsRef = database.getReference("Boards")

    private val _noteData = MutableLiveData<List<CheckNoteItem>>()
    val noteData: LiveData<List<CheckNoteItem>>
        get() = _noteData

    private val _success = MutableLiveData<Board>()
    val success: LiveData<Board>
        get() = _success

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private val _boardLiveData = MutableLiveData<Board>()
    val boardLiveData: LiveData<Board>
        get() = _boardLiveData

    fun deleteNote(note: Note, board: Board, listOfNotesItem: ListOfNotesItem) {
        updated = true
        viewModelScope.launch(Dispatchers.IO) {
            databaseNotesRef.child(note.id).removeValue()
            databaseListsOfNotesRef.child(board.id).child(listOfNotesItem.id)
                .child("listNotes").child(note.id).removeValue()
        }

    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            databaseNotesRef.child(note.id).setValue(note)
            val checkList = ArrayList<CheckNoteItem>()
//        listNotes as HashMap<String, Boolean>
//        listNotes.put(note.id, true)
            checkList.addAll(note.listOfTasks)
            _noteData.postValue(checkList)
        }

    }

    fun createNewNote(
        title: String,
        description: String,
        board: Board,
        listOfNotesItem: ListOfNotesItem,
        user: User,
        checkList: List<CheckNoteItem> = emptyList()
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val childListNotesRef = databaseListsOfNotesRef
                .child(board.id).child(listOfNotesItem.id).child("listNotes")
            val url = childListNotesRef.push()
            val idNote = url.key ?: ""
            val listNotes = listOfNotesItem.listNotes

            val note = Note(idNote, title, user.id, emptyList(), description, "", checkList)
            databaseNotesRef.child(idNote).setValue(note)
            listNotes as HashMap<String, Boolean>
            listNotes.put(idNote, true)
            listOfNotesItem.listNotes = listNotes
            url.setValue(true)
            updated = true
            _success.postValue(board)
        }

    }

    fun moveNote(note: Note, listOfNotesItem: ListOfNotesItem, board: Board, user: User) {
        updated = true
        deleteNote(note, board, listOfNotesItem)
        createNewNote(note.title, note.description, board, listOfNotesItem, user, note.listOfTasks)
    }
}