package com.example.taskscheduler.presentation.newboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.MyDatabaseConnection
import com.example.taskscheduler.domain.BackgroundImage
import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.domain.usecases.AddBoardUseCase
import com.example.taskscheduler.domain.usecases.GetBoardUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class NewBoardViewModel @Inject constructor(
    private val addBoardUseCase: AddBoardUseCase,
    private val getBoardUseCase: GetBoardUseCase,
) : ViewModel() {

    private val _boardLiveData = MutableLiveData<Board>()
    val boardLiveData: LiveData<Board>
        get() = _boardLiveData

    private val _urlImage = MutableLiveData<List<BackgroundImage>>()
    val urlImage: LiveData<List<BackgroundImage>>
        get() = _urlImage

    init {
        _urlImage.value = MyDatabaseConnection.backgroundImages
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
                getBoardUseCase.execute(result.getOrThrow()).collectLatest {
                    _boardLiveData.postValue(it)
                }

            }
        }
    }

    private fun buildImageList(list: List<String>): List<BackgroundImage> {
        return list.mapIndexed { index, item ->
            BackgroundImage("$index", item, false)
        }
    }
}