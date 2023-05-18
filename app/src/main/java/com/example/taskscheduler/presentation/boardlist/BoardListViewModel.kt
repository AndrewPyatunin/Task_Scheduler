package com.example.taskscheduler.presentation.boardlist

import android.util.Log
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

class BoardListViewModel(user: User): ViewModel() {
    private val auth = Firebase.auth
    private val firebaseDatabase = Firebase.database
    val databaseBoardsReference = firebaseDatabase.getReference("Boards")
    val databaseUsersReference = firebaseDatabase.getReference("Users")

    private val _firebaseUser = MutableLiveData<FirebaseUser>()
    val firebaseUser: LiveData<FirebaseUser>
        get() = _firebaseUser

    private val _boardList = MutableLiveData<List<Board>>()
    val boardList: LiveData<List<Board>>
        get() = _boardList

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    init {
        auth.addAuthStateListener {
            if (it.currentUser == null) {
                _firebaseUser.value = auth.currentUser
            }
        }
        auth.addAuthStateListener {
            if (it.currentUser != null) {
                databaseBoardsReference.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        readData(object : MyCallback {
                            override fun onCallback(user: User) {
                                val boardsId = user.boards ?: emptyList()
                                Log.i("BOARD_ID", boardsId.toString())
                                val boardsFromDb = ArrayList<Board>()
                                for (dataSnapshot in snapshot.children) {
                                    val board = dataSnapshot.getValue(Board::class.java)
                                    if (board != null && dataSnapshot.key in boardsId) {
                                        boardsFromDb.add(board)
                                    }
                                }
                                Log.i("BOARDS_FROM_DB", boardsFromDb.forEach { it.name + " " }.toString())
                                _boardList.value = boardsFromDb
//                                _success.value = it.currentUser
                            }

                        })

                    }

                    override fun onCancelled(error: DatabaseError) {
                        logout()
                    }

                })

            }
        }

//        databaseBoardsReference.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val boardsId = user.boards ?: emptyList()
//                Log.i("BOARD_ID", boardsId.toString())
//                val boardsFromDb = ArrayList<Board>()
//                for (dataSnapshot in snapshot.children) {
//                    val board = dataSnapshot.getValue(Board::class.java)
//                    if (board != null && dataSnapshot.key in boardsId) {
//                        boardsFromDb.add(board)
//                    }
//                }
//                Log.i("BOARDS_FROM_DB", boardsFromDb.forEach { it.name + " " }.toString())
//                _boardList.value = boardsFromDb
//
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                logout()
//            }
//
//        })
}

    fun readData(callback: MyCallback) {
        databaseUsersReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var userFromDb: User? = User()
                for (userSnapshot in snapshot.children) {
                    if (userSnapshot.key == auth.currentUser?.uid) {
                        userFromDb = userSnapshot.getValue(User::class.java)
                        //Log.i("BOARD_FROM_USER", userFromDb.boards[0])
                    }
                    if(userFromDb != null && userFromDb.id == auth.currentUser?.uid) {

                    }
                }
                //Log.i("BOARD_USER", userFromDb?.name)
                _user.value = userFromDb as User
                callback.onCallback(userFromDb)
            }

            override fun onCancelled(error: DatabaseError) {
                logout()
            }

        })
    }

    fun logout() {
        auth.signOut()
    }

    interface MyCallback {
        fun onCallback(user: User)
    }
}