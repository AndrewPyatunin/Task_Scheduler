package com.example.taskscheduler.presentation.boardupdated

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.taskscheduler.domain.Board
import com.example.taskscheduler.domain.ListOfNotesItem
import com.example.taskscheduler.domain.Note
import com.example.taskscheduler.domain.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class OuterBoardViewModel : ViewModel() {
    val database = Firebase.database
    val databaseListsOfNotesRef = database.getReference("ListsOfNotes")
    val databaseNotesRef = database.getReference("Notes")
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

//    private val _listNotesLiveData = MutableLiveData<List<Note>>()
//    val listNotesLiveData: LiveData<List<Note>>
//        get() = _listNotesLiveData

    fun createNewList(title: String, board: Board, user: User): ListOfNotesItem{
        val listOfNotesIds = ArrayList<String>()
        val ref = databaseListsOfNotesRef.child(board.id).push()
        val listId = ref.key.toString()
        readData(board.id)
        val item = ListOfNotesItem(listId, title, user.id, emptyMap())
        ref.setValue(item)
        databaseBoardsRef.child(board.id)
            .addListenerForSingleValueEvent(object :
            ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                databaseBoardsRef.removeEventListener(this)
                if (snapshot.hasChild("listsOfNotesIds")) {
                    for (dataSnapshot in snapshot
                        .child("listsOfNotesIds").children) {
                        listOfNotesIds.add(dataSnapshot.value.toString())
                    }
                }
                listOfNotesIds.add(listId)
                databaseBoardsRef.child(board.id)
                    .child("listsOfNotesIds").setValue(listOfNotesIds)
                board.listsOfNotesIds = listOfNotesIds
                _boardLiveData.value = board
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        return item
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
//    fun getNotes(listNotesIds: List<String>) {
//        databaseNotesRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val listNotes = ArrayList<Note>()
//                for (dataSnapshot in snapshot.children) {
//                    if (dataSnapshot.key in listNotesIds) {
//                        dataSnapshot.getValue(Note::class.java)?.let { listNotes.add(it) }
//                    }
//                }
//                _listNotesLiveData.value = listNotes
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//            }
//
//        })
//    }
}