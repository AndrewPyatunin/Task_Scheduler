package com.example.taskscheduler.presentation.boardlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.MyApp
import com.example.taskscheduler.MyDatabaseConnection
import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.domain.usecases.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class BoardListViewModel : ViewModel() {

    private val boardRepository = MyApp.boardRepository
    private val userRepository = MyApp.userRepository
    private val getBoardsFlowUseCase: GetBoardsFlowUseCase = GetBoardsFlowUseCase(boardRepository)
    private val getUserFromRoomUseCase: GetUserFromRoomUseCase =
        GetUserFromRoomUseCase(MyApp.userRepository)
    private val addAllUsersUseCase = AddAllUsersUseCase(userRepository)
    private val fetchBoardsUseCase: FetchBoardsUseCase = FetchBoardsUseCase(boardRepository)
    private val clearAllDataInRoomUseCase = ClearAllDataInRoomUseCase(
        MyApp.inviteRepository,
        boardRepository,
        MyApp.notesListRepository,
        MyApp.noteRepository,
        userRepository
    )
    private val auth: FirebaseAuth = Firebase.auth

    private val _userLiveData = MutableLiveData<User>()
    val userLiveData: LiveData<User> = _userLiveData

    private val _boardsLiveData = MutableLiveData<List<Board>>()
    val boardsLiveData: LiveData<List<Board>> = _boardsLiveData

    private val _dataReady = MutableLiveData<Unit>()
    val dataReady: LiveData<Unit> = _dataReady

    init {
        viewModelScope.launch(Dispatchers.IO) {
            addAllUsersUseCase.execute(viewModelScope)
        }

    }

    fun fetchUser() {
        viewModelScope.launch(Dispatchers.IO) {
            getUser().let {
                _userLiveData.postValue(it)
            }
        }
    }

    fun fetchBoards(user: User, listBoards: List<Board>) {
        viewModelScope.launch(Dispatchers.IO) {
            _dataReady.postValue(fetchBoardsUseCase.execute(user, viewModelScope, listBoards))
        }
    }


    fun getBoardsFlow(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            getBoardsFlowUseCase.execute(user).map {
                it.filter {
                    it.id in user.boards
                }
            }.collect {
                _boardsLiveData.postValue(it)
            }
        }

    }

    private suspend fun getUser() =
        getUserFromRoomUseCase.execute(auth.currentUser?.uid ?: MyDatabaseConnection.userId!!)

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            clearAllDataInRoomUseCase.execute()
        }
        auth.signOut()
    }
}