package com.example.taskscheduler.presentation.newboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.MyApp
import com.example.taskscheduler.MyDatabaseConnection
import com.example.taskscheduler.domain.BackgroundImage
import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.domain.usecases.AddBoardUseCase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewBoardViewModel : ViewModel() {

    private val boardRepository = MyApp.boardRepository
    private val addBoardUseCase = AddBoardUseCase(boardRepository)

    private val _boardLiveData = MutableLiveData<Board>()
    val boardLiveData: LiveData<Board>
        get() = _boardLiveData

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _urlImage = MutableLiveData<List<BackgroundImage>>()
    val urlImage: LiveData<List<BackgroundImage>>
        get() = _urlImage

    init {
        _urlImage.value = MyDatabaseConnection.list
    }

    fun createNewBoard(
        name: String,
        user: User,
        urlBackground: String,
        board: Board
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = addBoardUseCase.execute(name, user, urlBackground, board)
            if (result.isSuccess) {
                _boardLiveData.postValue(boardRepository.getBoard(result.getOrThrow()))
            }
        }
    }

    private fun buildImageList(list: List<String>): List<BackgroundImage> {
        return list.mapIndexed { index, item ->
            BackgroundImage("$index", item, false)
        }
    }
}