package com.example.taskscheduler.presentation.boardupdated

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.MyApp
import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.NotesListItem
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.domain.usecases.AddNotesListItemUseCase
import com.example.taskscheduler.domain.usecases.FetchNotesListUseCase
import com.example.taskscheduler.domain.usecases.GetBoardUseCase
import com.example.taskscheduler.domain.usecases.GetNotesListsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class OuterBoardViewModel : ViewModel() {

    private val notesListRepository = MyApp.notesListRepository
    private val addNotesListItemUseCase = AddNotesListItemUseCase(notesListRepository)
    private val getNotesListsUseCase = GetNotesListsUseCase(notesListRepository)
    private val fetchNotesListUseCase = FetchNotesListUseCase(notesListRepository)
    private val getBoardUseCase = GetBoardUseCase(MyApp.boardRepository)

    private val _boardLiveData = MutableLiveData<Board>()
    val boardLiveData: LiveData<Board>
        get() = _boardLiveData

    private val _listLiveData = MutableLiveData<List<NotesListItem>>()
    val listLiveData: LiveData<List<NotesListItem>>
        get() = _listLiveData

    private val _listReady = MutableLiveData<Unit>()
    val listReady: LiveData<Unit> = _listReady

    fun createNewList(title: String, board: Board, user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            addNotesListItemUseCase.execute(title, board, user)
            getBoard(board)
        }
    }

    fun fetchNotesLists(board: Board, listOfNotesList: List<NotesListItem>) {
        viewModelScope.launch(Dispatchers.IO) {
            wait(board, listOfNotesList)
        }
    }

    fun readData(board: Board) {
        viewModelScope.launch(Dispatchers.IO) {
            getNotesListsUseCase.execute(board).collect {
                _listLiveData.postValue(it)
            }
        }
    }

    suspend fun getBoard(board: Board) {
        _boardLiveData.postValue(getBoardUseCase.execute(board.id))
    }

    private suspend fun wait(board: Board, listOfNotesList: List<NotesListItem>) {

        val job = viewModelScope.async {
            _listReady.postValue(
                fetchNotesListUseCase.execute(
                    board.id,
                    viewModelScope,
                    listOfNotesList
                )
            )
        }
        val result = withTimeoutOrNull(2000) {
            job.await()
        }
        if (result == null) {
            _listReady.postValue(Unit)
        }
    }
}