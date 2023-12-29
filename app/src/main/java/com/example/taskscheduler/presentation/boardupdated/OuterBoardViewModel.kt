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
import com.example.taskscheduler.domain.usecases.GetNotesListsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OuterBoardViewModel : ViewModel() {

    private val notesListRepository = MyApp.notesListRepository
    private val addNotesListItemUseCase = AddNotesListItemUseCase(notesListRepository)
    private val getNotesListsUseCase = GetNotesListsUseCase(notesListRepository)
    private val fetchNotesListUseCase = FetchNotesListUseCase(notesListRepository)

    private val _liveData = MutableLiveData<NotesListItem>()
    val livedata: LiveData<NotesListItem>
        get() = _liveData

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

    fun fetchNotesLists(board: Board) {
        viewModelScope.launch(Dispatchers.IO) {
            _listReady.postValue(fetchNotesListUseCase.execute(board.id, viewModelScope))
        }
    }

    fun readData(board: Board) {
        viewModelScope.launch(Dispatchers.IO) {
            getNotesListsUseCase.execute(board).collect {
                _listLiveData.postValue(it)
            }
        }
    }
}