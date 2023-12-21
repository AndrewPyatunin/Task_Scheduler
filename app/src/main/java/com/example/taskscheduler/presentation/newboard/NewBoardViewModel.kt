package com.example.taskscheduler.presentation.newboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.taskscheduler.MyDatabaseConnection
import com.example.taskscheduler.TaskDatabase
import com.example.taskscheduler.data.repos.TaskRepositoryImpl
import com.example.taskscheduler.domain.BackgroundImage
import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.*

class NewBoardViewModel : AndroidViewModel(Application()) {

    private val firebaseDatabase = Firebase.database
    private val databaseBoardsReference = firebaseDatabase.getReference("Boards")
    val databaseUsersReference = firebaseDatabase.getReference("Users")
    var taskDatabase: TaskDatabase? = null

    private val repository = TaskRepositoryImpl()

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

    init {
        taskDatabase = TaskDatabase.getInstance(this.getApplication())
        _urlImage.value = MyDatabaseConnection.list
    }

    fun createNewBoard(
        name: String,
        user: User,
        urlBackground: String,
        board: Board
    ) = repository.createNewBoard(name, user, urlBackground, board)


    private fun buildImageList(list: List<String>): List<BackgroundImage> {
        return list.mapIndexed { index, item ->
            BackgroundImage("$index", item, false)
        }
    }


    fun logout() {
        Firebase.auth.signOut()
    }

}