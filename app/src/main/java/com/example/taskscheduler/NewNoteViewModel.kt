package com.example.taskscheduler

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.taskscheduler.domain.Board
import com.example.taskscheduler.domain.ListOfNotesItem
import com.example.taskscheduler.domain.Note
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class NewNoteViewModel: ViewModel() {
    val database = Firebase.database
    val databaseListsOfNotesRef = database.getReference("ListsOfNotes")
    val databaseBoardsRef = database.getReference("Boards")

    private val _noteData = MutableLiveData<Note>()
    val noteData: LiveData<Note>
        get() = _noteData

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private val _boardLiveData = MutableLiveData<Board>()
    val boardLiveData: LiveData<Board>
        get() = _boardLiveData


    fun updateNote() {

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
        url.setValue(note)
        _noteData.value = note

//        databaseBoardsRef.child(board.id).child("listsOfNotes").addListenerForSingleValueEvent(object :
//            ValueEventListener {
//
//            override fun onDataChange(snapshot: DataSnapshot) {
//
//                databaseBoardsRef.removeEventListener(this)
//                var listNotesIds = ArrayList<String>()
//                if (snapshot.value == null) {
////                    listNotesIds.add(idListOfNotes)
//                } else {
//                    for (dataSnapshot in snapshot.children) {
//                        val data = dataSnapshot.value as String
//                        Log.i("VALUE_CHILD_FROM_USERS", data)
//                        listNotesIds.add(data)
//                    }
//                    listNotesIds.add(idNote)
//                }
////                val listMembersIds = ArrayList<String>()
////                listMembersIds.add(user.id)
//
//                Log.i("VALUE_BOARD_ID", idNote)
//                val urlList = databaseListsOfNotesRef.child(idNote).child("listOfNotes").push()
//                val noteId = urlList.key.toString()
//                val note = Note(noteId, title, emptyList(), description, "", emptyList())
//
//                urlList.setValue(note)
//
//
////                val userToDb =
////                    User(user.id, user.name, user.lastName, user.email, true, listNotesIds, user.uri)
//                val boardToDb = Board(board.id, board.name, board.backgroundUrl, board.members, listNotesIds)
//                databaseBoardsRef.child(board.id).child("listOfNotes").setValue(listNotesIds)
////                _user.value?.boards = listBoardsIds
////                _user.value = userToDb
//                _boardLiveData.value = boardToDb
//                _noteData.value = note
////                    callback.onBoardListCallback(listBoardsIds, userToDb)
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                _error.value = error.message
////                logout()
//            }
//
//        })

    }
}