package com.example.taskscheduler

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.taskscheduler.domain.Board
import com.example.taskscheduler.domain.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
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

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    fun createNewBoard(name: String, user: User) {
        //Log.i("FIREBASE_USER", "${Firebase.auth.currentUser?.email}")
        Log.i("USER_FROM_LIST", user.id)
        val url = databaseBoardsReference.push()
        val idBoard = url.key ?: ""
        databaseUsersReference.child(user.id).addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                databaseUsersReference.removeEventListener(this)
                var listBoardsIds = ArrayList<String>()
                if (snapshot.child("boards").value == null) {
                    listBoardsIds.add(idBoard)
                } else {
                    for (dataSnapshot in snapshot.child("boards").children) {
                        val data = dataSnapshot.value as String
                        Log.i("VALUE_CHILD_FROM_USERS", data)
                        listBoardsIds.add(data)
                    }
                    listBoardsIds.add(idBoard)
                }
                val listMembersIds = ArrayList<String>()
                listMembersIds.add(user.id)

                Log.i("VALUE_BOARD_ID", idBoard)
                val board = Board(idBoard, name, listMembersIds, emptyMap())

                url.setValue(board)
                _boardLiveData.value = board

                val userToDb =
                    User(user.id, user.name, user.lastName, user.email, true, listBoardsIds)
                databaseUsersReference.child(user.id).child("boards").setValue(listBoardsIds)
//                _user.value?.boards = listBoardsIds
                _user.value = userToDb
//                    callback.onBoardListCallback(listBoardsIds, userToDb)
            }

            override fun onCancelled(error: DatabaseError) {
                _error.value = error.message
                logout()
            }

        })


    }


//        _user.value = Firebase.auth.currentUser


    fun logout() {
        Firebase.auth.signOut()
    }



    interface BoardListCallback {
        fun onBoardListCallback(listBoardsIds: ArrayList<String>, user: User)
    }
}