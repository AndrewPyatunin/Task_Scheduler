package com.example.taskscheduler.presentation.newboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.taskscheduler.domain.BackgroundImage
import com.example.taskscheduler.domain.Board
import com.example.taskscheduler.domain.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class NewBoardViewModel : ViewModel() {
    private val firebaseDatabase = Firebase.database
    private val databaseBoardsReference = firebaseDatabase.getReference("Boards")
    val databaseUsersReference = firebaseDatabase.getReference("Users")
    private val databaseImageUrlsReference = firebaseDatabase.getReference("ImageUrls")
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
        databaseImageUrlsReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val imageUrls = ArrayList<String>()
                for (dataSnapshot in snapshot.children) {
                    Log.i("USER_IMAGES", dataSnapshot.value.toString())
                    imageUrls.add(dataSnapshot.value.toString())
                }

                _urlImage.value = buildImageList(imageUrls)
            }

            override fun onCancelled(error: DatabaseError) {
                _error.value = error.message
                logout()
            }

        })
    }

    private fun buildImageList(list: List<String>): ArrayList<BackgroundImage> {
        val listOfBackgroundImage = ArrayList<BackgroundImage>()
        for ((i, item) in list.withIndex()) {
            listOfBackgroundImage.add(BackgroundImage("$i", item, false))
        }
        return listOfBackgroundImage
    }

    fun recyclerIsReady() {
        Log.i("USER_RECYCLER", "12")
//        _recyclerIsReady.value = 1
    }


    fun createNewBoard(name: String, user: User, urlBackground: String) {
        val urlForBoard = databaseBoardsReference.push()
        val idBoard = urlForBoard.key ?: ""

        databaseUsersReference.child(user.id)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    databaseUsersReference.removeEventListener(this)
                    val listBoardsIds = ArrayList<String>()
                    if (snapshot.child("boards").value == null) {
                        listBoardsIds.add(idBoard)
                    } else {
                        for (dataSnapshot in snapshot.child("boards").children) {
                            val data = dataSnapshot.value as String
                            listBoardsIds.add(data)
                        }
                        listBoardsIds.add(idBoard)
                    }
                    val listMembersIds = ArrayList<String>()
                    listMembersIds.add(user.id)
                    val board = Board(idBoard, name, urlBackground, listMembersIds, emptyList())
                    urlForBoard.setValue(board)
                    val userToDb =
                        User(
                            user.id,
                            user.name,
                            user.lastName,
                            user.email,
                            true,
                            listBoardsIds,
                            user.uri
                        )
                    databaseUsersReference.child(user.id).child("boards")
                        .setValue(listBoardsIds)
                    _user.value = userToDb
                    _boardLiveData.value = board
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