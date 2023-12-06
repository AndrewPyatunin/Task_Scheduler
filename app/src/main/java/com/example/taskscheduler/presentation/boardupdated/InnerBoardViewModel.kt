package com.example.taskscheduler.presentation.boardupdated

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.ListOfNotesItem
import com.example.taskscheduler.domain.models.Note
import com.example.taskscheduler.domain.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class InnerBoardViewModel : ViewModel() {
    private val database = Firebase.database
    private val databaseNotesRef = database.getReference("Notes")
    private val databaseBoardsRef = database.getReference("Boards")
    private val databaseListsOfNotesRef = database.getReference("ListsOfNotes")
    private val databaseUsersRef = database.getReference("Users")

    private val _listNotesLiveData = MutableLiveData<List<Note>>()
    val listNotesLiveData: LiveData<List<Note>>
        get() = _listNotesLiveData

    fun getNotes(listNotesIds: List<String>) {
        databaseNotesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listNotes = ArrayList<Note>()
                for (dataSnapshot in snapshot.children) {
                    if (dataSnapshot.key in listNotesIds) {
                        dataSnapshot.getValue(Note::class.java)?.let { listNotes.add(it) }
                    }
                }
                _listNotesLiveData.value = listNotes
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun updateBoard(board: Board, listOfNotesItemId: String) {

        val listOfNotesIdsNew = board.listsOfNotesIds
        (listOfNotesIdsNew as ArrayList).remove(listOfNotesItemId)
        databaseBoardsRef.child(board.id).child("listsOfNotesIds").setValue(listOfNotesIdsNew)
    }

    fun deleteList(listOfNotesItemId: String, board: Board, isList: Boolean) {
        databaseListsOfNotesRef.child(board.id).child(listOfNotesItemId).removeValue()
        val listNotes = _listNotesLiveData.value
        if (listNotes != null) {
            for (note in listNotes) {
                databaseNotesRef.child(note.id).removeValue()
            }
        }
        if (isList) updateBoard(board, listOfNotesItemId)
    }



    fun renameList(listOfNotesItem: ListOfNotesItem, board: Board, title: String) {
        databaseListsOfNotesRef.child(board.id).child(listOfNotesItem.id)
            .child("title").setValue(title)
    }

    fun deleteBoard(board: Board, user: User) {
        databaseBoardsRef.child(board.id).removeValue()
        val listBoardsIds = user.boards as ArrayList<String>
        listBoardsIds.remove(board.id)
        databaseUsersRef.child(user.id).child("boards").setValue(listBoardsIds)
        for (list in board.listsOfNotesIds)
            deleteList(list, board, false)
    }
}