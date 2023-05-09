package com.example.taskscheduler

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.taskscheduler.domain.Board
import com.example.taskscheduler.domain.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val firebaseDatabase = Firebase.database
    val databaseBoardsReference = firebaseDatabase.getReference("Boards")
    val databaseUsersReference = firebaseDatabase.getReference("Users")


    private val _boardList = MutableLiveData<ArrayList<Board>>()
    val boardList: LiveData<ArrayList<Board>>
        get() = _boardList

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    init {

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
                                _success.value = it.currentUser
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

    private val _success = MutableLiveData<FirebaseUser>()
    val success: LiveData<FirebaseUser>
        get() = _success

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
        }.addOnFailureListener {
            _error.value = it.message
        }
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