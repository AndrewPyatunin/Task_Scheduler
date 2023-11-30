package com.example.taskscheduler.presentation.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.taskscheduler.MyDatabaseConnection
import com.example.taskscheduler.data.TaskDatabase
import com.example.taskscheduler.data.TaskRepositoryImpl
import com.example.taskscheduler.domain.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginViewModel : AndroidViewModel(Application()) {
    private val auth: FirebaseAuth = Firebase.auth
    private var taskDatabase: TaskDatabase? = null
    private var user: User? = null
//    private val firebaseDatabase = Firebase.database
//    val databaseBoardsReference = firebaseDatabase.getReference("Boards")
//    val databaseUsersReference = firebaseDatabase.getReference("Users")


//    private val _boardList = MutableLiveData<ArrayList<Board>>()
//    val boardList: LiveData<ArrayList<Board>>
//        get() = _boardList

    private val _userLiveData = MutableLiveData<User>()
    val userLiveData: LiveData<User>
        get() = _userLiveData

    private val repository by lazy {
        TaskRepositoryImpl()
    }

    private val _success = MutableLiveData<User>()
    val success: LiveData<User>
        get() = _success
    init {

        taskDatabase = TaskDatabase.getInstance(getApplication())
        auth.addAuthStateListener {
            //val userDb = taskDatabase?.taskDatabaseDao()?.getUser(it.currentUser?.email ?: "")
            if (it.currentUser != null) {
                _success.value = user ?: User()
            }
        }

//        auth.addAuthStateListener {
//            if (it.currentUser != null) {
//                databaseBoardsReference.addValueEventListener(object : ValueEventListener {
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        readData(object : MyCallback {
//                            override fun onCallback(user: User) {
//                                val boardsId = user.boards ?: emptyList()
//                                Log.i("BOARD_ID", boardsId.toString())
//                                val boardsFromDb = ArrayList<Board>()
//                                for (dataSnapshot in snapshot.children) {
//                                    val board = dataSnapshot.getValue(Board::class.java)
//                                    if (board != null && dataSnapshot.key in boardsId) {
//                                        boardsFromDb.add(board)
//                                    }
//                                }
//                                Log.i("BOARDS_FROM_DB", boardsFromDb.forEach { it.name + " " }.toString())
//                                _boardList.value = boardsFromDb
//                                _success.value = it.currentUser
//                            }
//
//                        })
//
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//                        logout()
//                    }
//
//                })
//
//            }
//        }

    }



    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    fun login(email: String, password: String) {
        user = taskDatabase?.taskDatabaseDao()?.getUser(email)
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
        }.addOnFailureListener {
            _error.value = it.message
        }
        if (password == user?.password) {
            MyDatabaseConnection.userId = user?.id
            _success.value = user ?: User()
        }
    }

//    fun readData(callback: MyCallback) {
//        databaseUsersReference.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                var userFromDb: User? = User()
//                for (userSnapshot in snapshot.children) {
//                    if (userSnapshot.key == auth.currentUser?.uid) {
//                        userFromDb = userSnapshot.getValue(User::class.java)
//                        //Log.i("BOARD_FROM_USER", userFromDb.boards[0])
//                    }
//                    if(userFromDb != null && userFromDb.id == auth.currentUser?.uid) {
//
//                    }
//                }
//                //Log.i("BOARD_USER", userFromDb?.name)
//                _user.value = userFromDb as User
//                callback.onCallback(userFromDb)
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                logout()
//            }
//
//        })
//    }

    fun logout() {
        auth.signOut()
    }
//
//    interface MyCallback {
//        fun onCallback(user: User)
//    }
}