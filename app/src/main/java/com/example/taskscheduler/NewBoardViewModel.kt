package com.example.taskscheduler

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.taskscheduler.domain.Board
import com.example.taskscheduler.domain.User
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class NewBoardViewModel : ViewModel() {
    private val firebaseDatabase = Firebase.database
    val databaseBoardsReference = firebaseDatabase.getReference("Boards")
    val databaseUsersReference = firebaseDatabase.getReference("Users")

    private val _boardLiveData = MutableLiveData<Board>()
    val boardLiveData: LiveData<Board>
        get() = _boardLiveData

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    fun createNewBoard(name: String, user: User) {
        val listMembersIds = ArrayList<String>()
        listMembersIds.add(user.id)
        val url = databaseBoardsReference.push()
        val idBoard = url.key ?: ""
        val board = Board(idBoard, name, listMembersIds, emptyMap())
        databaseUsersReference.child(user.id).setValue(user)
        url.setValue(board)
        _boardLiveData.value = board
        _user.value = user

//        _user.value = Firebase.auth.currentUser

    }
}