package com.example.taskscheduler.presentation.boardupdated

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.MyApp
import com.example.taskscheduler.domain.models.*
import com.example.taskscheduler.domain.usecases.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class InnerBoardViewModel : ViewModel() {

    private val boardRepository = MyApp.boardRepository
    private val notesListRepository = MyApp.notesListRepository
    private val noteRepository = MyApp.noteRepository
    private val removeBoardUseCase = RemoveBoardUseCase(boardRepository)
    private val removeNotesListItemUseCase = RemoveNotesListItemUseCase(boardRepository)
    private val renameListUseCase = RenameListUseCase(notesListRepository)
    private val getNotesUseCase = GetNotesUseCase(noteRepository)
    private val fetchNotesUseCase = FetchNotesUseCase(noteRepository)

    private val _listNotesLiveData = MutableLiveData<List<Note>>()
    val listNotesLiveData: LiveData<List<Note>>
        get() = _listNotesLiveData

    private val _readyLiveData = MutableLiveData<Unit>()
    val readyLiveData: LiveData<Unit> = _readyLiveData

    fun getNotes(listNotesIds: Map<String, Boolean>) {
        viewModelScope.launch(Dispatchers.IO) {
            getNotesUseCase.execute().map {
                it.filter {
                    it.id in listNotesIds
                }
            }.collect {
                _listNotesLiveData.postValue(it)
            }
        }
    }

    fun fetchNotes(notesListItem: NotesListItem, listNotes: List<Note>) {
        viewModelScope.launch(Dispatchers.IO) {
            fetchNotesUseCase.execute(notesListItem, listNotes, viewModelScope).let {
                _readyLiveData.postValue(it)
            }
        }
    }

    fun deleteList(notesListItem: NotesListItem, board: Board, isList: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            removeNotesListItemUseCase.execute(notesListItem, board, isList)
        }
    }


    fun renameList(notesListItem: NotesListItem, board: Board, title: String) {
        viewModelScope.launch(Dispatchers.IO) {
            renameListUseCase.execute(notesListItem, board, title)
        }
    }

    fun deleteBoard(board: Board, user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            removeBoardUseCase.execute(board, user)
        }

    }
}