package com.example.taskscheduler.presentation.newnote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.domain.models.CheckNoteItem
import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.Note
import com.example.taskscheduler.domain.models.NotesListItem
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.domain.usecases.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

class NewNoteViewModel @Inject constructor(
    private val removeNoteUseCase: RemoveNoteUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val moveNoteUseCase: MoveNoteUseCase,
    private val getNoteUseCase: GetNoteUseCase,
    private val addNoteUseCase: AddNoteUseCase
) : ViewModel() {

    private val _noteLiveData = MutableLiveData<Note>()
    val noteLiveData: LiveData<Note> = _noteLiveData

    private val _noteData = MutableLiveData<List<CheckNoteItem>>()
    val noteData: LiveData<List<CheckNoteItem>>
        get() = _noteData

    private val _success = MutableLiveData<Board>()
    val success: LiveData<Board>
        get() = _success

    fun deleteNote(note: Note, board: Board, notesListItem: NotesListItem) {
        viewModelScope.launch(Dispatchers.IO) {
            removeNoteUseCase.execute(board, note, notesListItem)
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            updateNoteUseCase.execute(note)
            getNote(note.id)
        }
    }

    fun getNote(noteId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getNoteUseCase.execute(noteId).distinctUntilChanged().collect {
                _noteLiveData.postValue(it)
                _noteData.postValue(it.listOfTasks)
            }
        }
    }

    fun createNewNote(
        title: String,
        description: String,
        board: Board,
        notesListItem: NotesListItem,
        user: User,
        checkList: List<CheckNoteItem> = emptyList()
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            addNoteUseCase.execute(board, description, notesListItem, title, user)
            _success.postValue(board)
        }
    }

    fun moveNote(note: Note, fromNotesListItem: NotesListItem, notesListItem: NotesListItem, board: Board, user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            moveNoteUseCase.execute(fromNotesListItem, notesListItem, note, board, user) {
                getNote(it)
            }
        }
    }
}