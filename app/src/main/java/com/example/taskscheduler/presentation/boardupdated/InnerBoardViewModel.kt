package com.example.taskscheduler.presentation.boardupdated

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.Note
import com.example.taskscheduler.domain.models.NotesListItem
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.domain.usecases.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class InnerBoardViewModel @Inject constructor(
    private val removeBoardUseCase: RemoveBoardUseCase,
    private val removeNotesListItemUseCase: RemoveNotesListItemUseCase,
    private val renameListUseCase: RenameListUseCase,
    private val getNotesUseCase: GetNotesUseCase,
    private val fetchNotesUseCase: FetchNotesUseCase
) : ViewModel() {

    private val _listNotesLiveData = MutableLiveData<List<Note>>()
    val listNotesLiveData: LiveData<List<Note>>
        get() = _listNotesLiveData

    private val _readyLiveData = MutableLiveData<Unit>()
    val readyLiveData: LiveData<Unit> = _readyLiveData

    fun getNotes(listNotesIds: Map<String, Boolean>) {
        viewModelScope.launch(Dispatchers.IO) {
            getNotesUseCase.execute().map {
                it.filter { note ->
                    note.id in listNotesIds
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