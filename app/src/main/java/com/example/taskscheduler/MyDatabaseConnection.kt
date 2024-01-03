package com.example.taskscheduler

import android.app.Application
import android.net.Uri
import com.example.taskscheduler.domain.BackgroundImage
import com.example.taskscheduler.domain.NewCallback
import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.NotesListItem
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.domain.repos.UserRepository
import com.example.taskscheduler.domain.usecases.AddAllUsersUseCase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope

object MyDatabaseConnection : DatabaseConnection {

    val database = Firebase.database
    private val databaseImagesReference = database.getReference("ImageUrls")
    lateinit var repository: UserRepository
    private var addAllUsersUseCase: AddAllUsersUseCase? = null
    @Volatile var boardList = emptyList<Board>()
    @Volatile var parentList = emptyList<NotesListItem>()
    var user = User()
    var uri: Uri? = null
    var currentPosition = 0
    var list = emptyList<BackgroundImage>()
    var updated = true
    var userId: String? = null

    fun onCallbackReady() {
        getBackgroundImages(object : NewCallback {
            override fun callbackNew(list: List<BackgroundImage>) {
                this@MyDatabaseConnection.list = list
            }

        })
    }
    override fun getBackgroundImages(callback: NewCallback)  {
        databaseImagesReference.get().addOnSuccessListener {
            val items = it.children.mapIndexed { index, dataSnapshot ->
                BackgroundImage("$index", dataSnapshot.value.toString(), false)
            }
            callback.callbackNew(items)
        }
    }

    override suspend fun query(application: Application, scope: CoroutineScope) {
        if (addAllUsersUseCase == null) {
            repository = MyApp.userRepository
            addAllUsersUseCase = AddAllUsersUseCase(repository)
        }
        addAllUsersUseCase?.execute(scope)
    }

    override fun getBoard() {
        TODO("Not yet implemented")
    }

    override fun getBackgroundImages(): List<BackgroundImage> {
        TODO("Not yet implemented")
    }

}
