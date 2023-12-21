package com.example.taskscheduler.data.repos

import androidx.lifecycle.LiveData
import com.example.taskscheduler.data.datasources.NoteDataSource
import com.example.taskscheduler.data.datasources.NotesListDataSource
import com.example.taskscheduler.data.entities.NotesListEntity
import com.example.taskscheduler.data.mappers.Mapper
import com.example.taskscheduler.domain.repos.BoardRepository
import com.example.taskscheduler.domain.repos.NotesListRepository
import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.NotesListItem
import com.example.taskscheduler.domain.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class NotesListRepositoryImpl(
    private val notesListDataSource: NotesListDataSource,
    private val noteDataSource: NoteDataSource,
    private val boardRepository: BoardRepository,
    private val notesListEntityToNotesListItemMapper: Mapper<NotesListEntity, NotesListItem>,
    private val notesListItemToNotesListEntityMapper: Mapper<NotesListItem, NotesListEntity>,
    private val databaseNotesListReference: DatabaseReference,
    private val databaseNoteReference: DatabaseReference,
    private val databaseBoardsReference: DatabaseReference
) : NotesListRepository {

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
            notesListItem.id,
            CoroutineScope(Dispatchers.IO)
        )
    }

    override suspend fun createNewList(title: String, board: Board, user: User): Board =
        suspendCoroutine {
            val listOfNotesIds = ArrayList<String>()
            val ref = databaseNotesListReference.child(board.id).push()
            val listId = ref.key.toString()
//        readData(board.id)
            val item = NotesListItem(listId, title, user.id, emptyMap())
            ref.setValue(item)
            databaseBoardsReference.child(board.id)
                .addListenerForSingleValueEvent(object :
                    ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {
                        databaseBoardsReference.removeEventListener(this)
                        if (snapshot.hasChild("listsOfNotesIds")) {
                            snapshot.child("listOfNotesIds").children.forEach {
                                listOfNotesIds.add(it.value.toString())
                            }
                        }
                        listOfNotesIds.add(listId)
                        databaseBoardsReference.child(board.id)
                            .child("listsOfNotesIds").setValue(listOfNotesIds)
                        val boardToDb = board.copy(listsOfNotesIds = listOfNotesIds)
                        it.resumeWith(Result.success(boardToDb))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        it.resumeWithException(error.toException())
                    }
                })
        }

    override fun getNotesLists(boardId: String): LiveData<List<NotesListItem>> {
        TODO("Not yet implemented")
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