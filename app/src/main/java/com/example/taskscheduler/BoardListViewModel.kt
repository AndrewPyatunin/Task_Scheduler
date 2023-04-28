package com.example.taskscheduler

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.taskscheduler.domain.Board
import com.example.taskscheduler.domain.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class BoardListViewModel: ViewModel() {
    private val auth = Firebase.auth
    private val firebaseDatabase = Firebase.database
    val databaseBoardsReference = firebaseDatabase.getReference("Boards")

    private val _user = MutableLiveData<FirebaseUser>()
    val user: LiveData<FirebaseUser>
        get() = _user

    private val _boardList = MutableLiveData<List<Board>>()
    val boardList: LiveData<List<Board>>
        get() = _boardList

    init {
        auth.addAuthStateListener {
            if (it.currentUser == null) {
                _user.value = auth.currentUser
            }
        }
        databaseBoardsReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val boardsFromDb = ArrayList<Board>()
                for (dataSnapshot in snapshot.children) {
                    val board = dataSnapshot.getValue(Board::class.java)
                    if (board != null) {
                        boardsFromDb.add(board)
                    }
                }
                _boardList.value = boardsFromDb
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun logout() {
        auth.signOut()
    }
}