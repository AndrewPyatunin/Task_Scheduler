package com.example.taskscheduler.presentation.board

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.taskscheduler.domain.Board
import com.example.taskscheduler.domain.ListOfNotesItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class BoardViewModel: ViewModel() {
    val database = Firebase.database
    val databaseListsOfNotesRef = database.getReference("ListsOfNotes")
    val databaseBoardsRef = database.getReference("Boards")

    private val _liveData = MutableLiveData<ListOfNotesItem>()
    val livedata: LiveData<ListOfNotesItem>
        get() = _liveData

    private val _boardLiveData = MutableLiveData<Board>()
    val boardLiveData: LiveData<Board>
        get() = _boardLiveData

    private val _listLiveData = MutableLiveData<List<ListOfNotesItem>>()
    val listLiveData: LiveData<List<ListOfNotesItem>>
        get() = _listLiveData

    fun createNewList(title: String, board: Board) {
        val listOfNotesIds = ArrayList<String>()
        val ref = databaseListsOfNotesRef.child(board.id).push()
        val listId = ref.key.toString()
        readData(board.id)
        ref.setValue(ListOfNotesItem(listId, title, emptyMap()))
        databaseBoardsRef.child(board.id).addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                databaseBoardsRef.removeEventListener(this)
                if (snapshot.hasChild("listOfNotes")) {
                    for (dataSnapshot in snapshot.child("listOfNotes").children) {
                        listOfNotesIds.add(dataSnapshot.value.toString())
                    }
                }
                listOfNotesIds.add(listId)
                databaseBoardsRef.child(board.id).child("listOfNotes").setValue(listOfNotesIds)
                board.listsOfNotesIds = listOfNotesIds
                _boardLiveData.value = board
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    fun readData(boardId: String) {
        databaseListsOfNotesRef.child(boardId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listOfNotesItem = ArrayList<ListOfNotesItem>()
                for (dataSnapshot in snapshot.children) {
                    val list = dataSnapshot.getValue(ListOfNotesItem::class.java)
                    if (list != null) listOfNotesItem.add(list)
                }
//                Log.i("USER_LIST_OF_NOTES", listOfNotesItem[0].title)
                _listLiveData.value = listOfNotesItem
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}