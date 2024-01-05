package com.example.taskscheduler.presentation.newnote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.MyApp
import com.example.taskscheduler.domain.CheckNoteItem
import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.Note
import com.example.taskscheduler.domain.models.NotesListItem
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.domain.usecases.AddNoteUseCase
import com.example.taskscheduler.domain.usecases.MoveNoteUseCase
import com.example.taskscheduler.domain.usecases.RemoveNoteUseCase
import com.example.taskscheduler.domain.usecases.UpdateNoteUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewNoteViewModel : ViewModel() {

    private val noteRepository = MyApp.noteRepository
    private val addNoteUseCase = AddNoteUseCase(noteRepository)
    private val removeNoteUseCase = RemoveNoteUseCase(noteRepository)
    private val updateNoteUseCase = UpdateNoteUseCase(noteRepository)
    private val moveNoteUseCase = MoveNoteUseCase(noteRepository)

    private val _noteData = MutableLiveData<List<CheckNoteItem>>()
    val noteData: LiveData<List<CheckNoteItem>>
        get() = _noteData

    private val _success = MutableLiveData<Board>()
    val success: LiveData<Board>
        get() = _success

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    fun deleteNote(note: Note, board: Board, notesListItem: NotesListItem) {
        viewModelScope.launch(Dispatchers.IO) {
            removeNoteUseCase.execute(board, note, notesListItem)
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            updateNoteUseCase.execute(note)
            _noteData.postValue(note.listOfTasks)
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
            moveNoteUseCase.execute(fromNotesListItem, notesListItem, note, board, user)
        }
    }
}