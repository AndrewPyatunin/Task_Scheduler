package com.example.taskscheduler.presentation.newboard

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.taskscheduler.MyDatabaseConnection
import com.example.taskscheduler.TaskDatabase
import com.example.taskscheduler.data.TaskRepositoryImpl
import com.example.taskscheduler.domain.BackgroundImage
import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class NewBoardViewModel : AndroidViewModel(Application()) {
    private val firebaseDatabase = Firebase.database
    private val databaseBoardsReference = firebaseDatabase.getReference("Boards")
    val databaseUsersReference = firebaseDatabase.getReference("Users")
    var taskDatabase: TaskDatabase? = null

    private val repository = TaskRepositoryImpl()

//    private val databaseImageUrlsReference = firebaseDatabase.getReference("ImageUrls")



    private val _boardLiveData = MutableLiveData<Board>()
    val boardLiveData: LiveData<Board>
        get() = _boardLiveData

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private val _urlImage = MutableLiveData<List<BackgroundImage>>()
    val urlImage: LiveData<List<BackgroundImage>>
        get() = _urlImage

//    private val _recyclerIsReady = MutableLiveData<Int>()
//    val recyclerIsReady: LiveData<Int>
//        get() = _recyclerIsReady

    init {
        taskDatabase = TaskDatabase.getInstance(this.getApplication())
        _urlImage.value = MyDatabaseConnection.list
//        databaseImageUrlsReference.addValueEventListener(object : ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                Log.i("USER_URL_IMAGE", Thread.currentThread().name)
//                val items = snapshot.children.mapIndexed { index, dataSnapshot ->
//                    BackgroundImage("$index", dataSnapshot.value.toString(), false)
//                }
//
//                _urlImage.postValue(items)
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                _error.postValue(error.message)
//                logout()
//            }
//
//        })



    }

    fun createNewBoard(name: String, user: User, urlBackground: String, board: Board) {
        repository.createNewBoard(name, user, urlBackground, board)
    }

    private fun buildImageList(list: List<String>): List<BackgroundImage> {
        return list.mapIndexed { index, item ->
            BackgroundImage("$index", item, false)
        }
    }

    fun recyclerIsReady() {
        Log.i("USER_RECYCLER", "12")
//        _recyclerIsReady.value = 1
    }


//    fun createNewBoard(name: String, user: User, urlBackground: String, board: Board) {
//        val urlForBoard = databaseBoardsReference.push()
//        val idBoard = urlForBoard.key ?: ""
//        taskDatabase?.taskDatabaseDao()?.getUser(1)?.collect()
//        if (board.id != "") {
//            viewModelScope.launch(Dispatchers.IO) {
//                Log.i("USER_CREATE", board.id)
//                val ref = databaseBoardsReference.child(board.id)
//                ref.child("backgroundUrl").setValue(urlBackground)
//                ref.child("title").setValue(name)
//                board.title = name
//            }
//
//            _boardLiveData.value = board
//        } else {
//            viewModelScope.launch(Dispatchers.IO) {
//                databaseUsersReference.child(user.id)
//                    .addListenerForSingleValueEvent(object : ValueEventListener {
//                        override fun onDataChange(snapshot: DataSnapshot) {
//                            databaseUsersReference.removeEventListener(this)
//                            val listBoardsIds = ArrayList<String>()
//                            if (snapshot.child("boards").value == null) {
//                                listBoardsIds.add(idBoard)
//                            } else {
//                                for (dataSnapshot in snapshot.child("boards")
//                                    .children) {
//                                    val data = dataSnapshot.value as String
//                                    listBoardsIds.add(data)
//                                }
//                                listBoardsIds.add(idBoard)
//                            }
//                            val listMembersIds = ArrayList<String>()
//                            listMembersIds.add(user.id)
//                            val board = Board(
//                                idBoard,
//                                name,
//                                user.id,
//                                urlBackground,
//                                listMembersIds,
//                                emptyList()
//                            )
//                            taskDatabase?.taskDatabaseDao()?.addBoard(board)
//                            urlForBoard.setValue(board)
//                            val userToDb =
//                                User(
//                                    user.id,
//                                    user.name,
//                                    user.lastName,
//                                    user.email,
//                                    true,
//                                    listBoardsIds,
//                                    user.uri
//                                )
//                            databaseUsersReference.child(user.id).child("boards")
//                                .setValue(listBoardsIds)
//                            taskDatabase?.taskDatabaseDao()?.addUser(userToDb)
//                            _user.value = userToDb
//                            _boardLiveData.value = board
//                        }
//
//                        override fun onCancelled(error: DatabaseError) {
//                            _error.value = error.message
//                            logout()
//                        }
//                    })
//            }
//        }
//    }


//        _user.value = Firebase.auth.currentUser


    fun logout() {
        Firebase.auth.signOut()
    }


    interface BoardListCallback {
        fun onBoardListCallback(listBoardsIds: ArrayList<String>, user: User)
    }
}