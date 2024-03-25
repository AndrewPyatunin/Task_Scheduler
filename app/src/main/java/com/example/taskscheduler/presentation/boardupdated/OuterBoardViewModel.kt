package com.example.taskscheduler.presentation.boardupdated

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.NotesListItem
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.domain.usecases.AddNotesListItemUseCase
import com.example.taskscheduler.domain.usecases.FetchNotesListUseCase
import com.example.taskscheduler.domain.usecases.GetBoardUseCase
import com.example.taskscheduler.domain.usecases.GetNotesListsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

class OuterBoardViewModel @Inject constructor(
    private val addNotesListItemUseCase: AddNotesListItemUseCase,
    private val getNotesListsUseCase: GetNotesListsUseCase,
    private val fetchNotesListUseCase: FetchNotesListUseCase,
    private val getBoardUseCase: GetBoardUseCase
) : ViewModel() {

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
        }
    }

    fun fetchNotesLists(board: Board, listOfNotesList: List<NotesListItem>) {
        fetchListOrDelay(board, listOfNotesList)
    }

    fun readData(board: Board) {
        viewModelScope.launch(Dispatchers.IO) {
            getNotesListsUseCase.execute(board).distinctUntilChanged().collect {
                _listLiveData.postValue(it)
            }
        }
    }

    fun getBoard(board: Board) {
        viewModelScope.launch(Dispatchers.IO) {
            getBoardUseCase.execute(board.id).distinctUntilChanged().collect {
                _boardLiveData.postValue(it)
            }
        }
    }

    private fun fetchListOrDelay(board: Board, listOfNotesList: List<NotesListItem>) {

        val jobFetch = viewModelScope.launch(Dispatchers.IO) {
            _listReady.postValue(
                fetchNotesListUseCase.execute(
                    board.id,
                    viewModelScope,
                    listOfNotesList
                )
            )
        }
        val jobDelay = viewModelScope.launch(Dispatchers.IO) {
            delay(3000)
        }
        viewModelScope.launch(Dispatchers.IO) {
            jobFetch.join()
            jobDelay.join()
            if (listReady.value != null) {
                return@launch
            } else {
                _listReady.value = Unit
            }
        }
    }
}