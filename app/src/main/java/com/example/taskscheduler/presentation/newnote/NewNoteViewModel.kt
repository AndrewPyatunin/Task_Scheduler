package com.example.taskscheduler.presentation.newnote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.taskscheduler.domain.Board
import com.example.taskscheduler.domain.CheckNoteItem
import com.example.taskscheduler.domain.ListOfNotesItem
import com.example.taskscheduler.domain.Note
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class NewNoteViewModel: ViewModel() {
    val database = Firebase.database
    private val databaseListsOfNotesRef = database.getReference("ListsOfNotes")
//    private val databaseNotesRef = database.getReference("Notes")
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


    fun updateNote(note: Note, board: Board, listOfNotesItem: ListOfNotesItem) {
        val ref = databaseListsOfNotesRef.child(board.id).child(listOfNotesItem.id).child("listNotes")
        val listNotes = listOfNotesItem.listNotes
        val checkList = ArrayList<CheckNoteItem>()

        listNotes as HashMap<String, Note>
        listNotes.put(note.id, note)
        checkList.addAll(note.listOfTasks)
        ref.setValue(listNotes)

        _noteData.value = checkList
    }

    fun createNewNote(title: String, description: String, board: Board, listOfNotesItem: ListOfNotesItem) {
        //Log.i("FIREBASE_USER", "${Firebase.auth.currentUser?.email}")
//        Log.i("BACKGROUND_URL", urlBackground)
//        Log.i("USER_FROM_LIST", user.id)
        val childListNotesRef = databaseListsOfNotesRef.child(board.id).child(listOfNotesItem.id).child("listNotes")
        val url = childListNotesRef.push()
        val idNote = url.key ?: ""
        val listNotes = listOfNotesItem.listNotes
//        childListNotesRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                for (dataSnapshot in snapshot.children) {
//                    val note = dataSnapshot.getValue(Note::class.java)
//                    if (note != null) listNotes.add(note)
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//
//        })

        val note = Note(idNote, title, emptyList(), description, "")
        listNotes as HashMap<String, Note>
        listNotes.put(idNote, note)
        listOfNotesItem.listNotes = listNotes
        url.setValue(note)
        _success.value = board
//        _noteData.value = note

    }
}