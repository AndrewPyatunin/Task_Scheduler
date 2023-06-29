package com.example.taskscheduler.presentation.boardlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.domain.Board
import com.example.taskscheduler.domain.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BoardListViewModel(user: User) : ViewModel() {
    private val auth = Firebase.auth
    private val firebaseDatabase = Firebase.database
    private val databaseBoardsReference = firebaseDatabase.getReference("Boards")
    private val databaseUsersReference = firebaseDatabase.getReference("Users")

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
                databaseBoardsReference.addValueEventListener(
                    object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            readData(object : MyCallback {
                                override fun onCallback(user: User) {
                                    val boardsId = user.boards
                                    val boardsFromDb = ArrayList<Board>()
                                    for (dataSnapshot in snapshot.children) {
                                        val board = dataSnapshot.getValue(Board::class.java)
                                        if (board != null && dataSnapshot.key in boardsId) {
                                            boardsFromDb.add(board)
                                        }
                                    }
                                    _boardList.value = boardsFromDb
                                }
                            })
                        }

                        override fun onCancelled(error: DatabaseError) {
                            logout()
                        }
                    })

            }
        }
    }

    fun readData(callback: MyCallback) {
        viewModelScope.launch(Dispatchers.IO) {
            val queryForUser = databaseUsersReference.child(auth.currentUser?.uid ?: "")
            queryForUser.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userFromDb = snapshot.getValue(User::class.java)
                    _user.postValue(userFromDb as User)
                    callback.onCallback(userFromDb)
                }

                override fun onCancelled(error: DatabaseError) {
                    logout()
                }
            })
        }

    }

    interface MyCallback {
        fun onCallback(user: User)
    }

    fun logout() {
        if (user.value != null) {
            databaseUsersReference.child(user.value!!.id).child("onlineStatus").setValue(false)
        }
        auth.signOut()
    }
}