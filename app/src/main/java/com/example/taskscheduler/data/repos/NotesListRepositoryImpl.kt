package com.example.taskscheduler.data.repos

import androidx.lifecycle.LiveData
import com.example.taskscheduler.data.FirebaseConstants.BOARDS
import com.example.taskscheduler.data.FirebaseConstants.NOTES
import com.example.taskscheduler.data.FirebaseConstants.NOTES_LIST
import com.example.taskscheduler.data.datasources.NoteDataSource
import com.example.taskscheduler.data.datasources.NotesListDataSource
import com.example.taskscheduler.data.entities.NotesListEntity
import com.example.taskscheduler.data.mappers.Mapper
import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.NotesListItem
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.domain.repos.BoardRepository
import com.example.taskscheduler.domain.repos.NotesListRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.coroutines.resumeWithException

class NotesListRepositoryImpl(
    private val notesListDataSource: NotesListDataSource,
    private val noteDataSource: NoteDataSource,
    private val boardRepository: BoardRepository,
    private val notesListEntityToNotesListItemMapper: Mapper<NotesListEntity, NotesListItem>,
    private val notesListItemToNotesListEntityMapper: Mapper<NotesListItem, NotesListEntity>,
) : NotesListRepository {

    private val databaseNotesListReference = Firebase.database.getReference(NOTES_LIST)
    private val databaseNoteReference = Firebase.database.getReference(NOTES)
    private val databaseBoardsReference = Firebase.database.getReference(BOARDS)

    override suspend fun renameList(notesListItem: NotesListItem, board: Board, title: String) {
        databaseNotesListReference.child(board.id).child(notesListItem.id)
            .child("title").setValue(title)
        addListOfNote(notesListItem.copy(title = title))
    }

    override suspend fun deleteList(notesListItem: NotesListItem, board: Board, isList: Boolean) {
        databaseNotesListReference.child(board.id).child(notesListItem.id).removeValue()
        notesListDataSource.removeListOfNotes(notesListItem.id)
        val listNotes =
            noteDataSource.getNotes().filter { it.id in notesListItem.listNotes.keys }
        listNotes.map {
            databaseNoteReference.child(it.id).removeValue()
            noteDataSource.removeNote(it)
        }
        if (isList) boardRepository.updateBoard(
            board,
            notesListItem.id
        )
    }

    override suspend fun createNewList(title: String, board: Board, user: User) {
        val ref = databaseNotesListReference.child(board.id).push()
        val listId = ref.key.toString()
        val item = NotesListItem(listId, title, user.id, emptyMap())
        ref.setValue(item)
        databaseBoardsReference.child(board.id)
            .child("listsOfNotesIds").updateChildren(mapOf(Pair(listId, true)))
        addListOfNote(item)
        val nodes = board.listsOfNotesIds as MutableMap
        nodes[listId] = true
        boardRepository.addBoard(board.copy(listsOfNotesIds = nodes))
    }

    override fun getNotesListsFlow(board: Board): Flow<List<NotesListItem>> {
        return notesListDataSource.getListsOfNotesFlow(board.id).map {
            it.map {
                notesListEntityToNotesListItemMapper.map(it)
            }
        }
    }

    override suspend fun addListOfNote(notesListItem: NotesListItem) {
        notesListDataSource.addListOfNotes(notesListItemToNotesListEntityMapper.map(notesListItem))
    }
}