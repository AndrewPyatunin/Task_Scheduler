package com.example.taskscheduler.data.repos

import com.example.taskscheduler.data.FirebaseConstants.BOARDS
import com.example.taskscheduler.data.FirebaseConstants.NOTES_LIST
import com.example.taskscheduler.data.FirebaseConstants.PATH_NOTES_LIST_IDS
import com.example.taskscheduler.data.FirebaseConstants.PATH_TITLE
import com.example.taskscheduler.data.database.BoardDao
import com.example.taskscheduler.data.database.NotesListDao
import com.example.taskscheduler.data.datasources.BoardDataSourceImpl
import com.example.taskscheduler.data.datasources.NotesListDataSourceImpl
import com.example.taskscheduler.data.mappers.BoardToBoardEntityMapper
import com.example.taskscheduler.data.mappers.NotesListEntityToNotesListItemMapper
import com.example.taskscheduler.data.mappers.NotesListItemToNotesListEntityMapper
import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.NotesListItem
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.domain.repos.NotesListRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NotesListRepositoryImpl(
    notesListDao: NotesListDao,
    boardDao: BoardDao,
) : NotesListRepository {

    private val notesListDataSource = NotesListDataSourceImpl(notesListDao)
    private val boardDataSource = BoardDataSourceImpl(boardDao)

    private val boardToBoardEntityMapper = BoardToBoardEntityMapper()
    private val notesListEntityToNotesListItemMapper = NotesListEntityToNotesListItemMapper()
    private val notesListItemToNotesListEntityMapper = NotesListItemToNotesListEntityMapper()
    private val databaseNotesListReference = Firebase.database.getReference(NOTES_LIST)
    private val databaseBoardsReference = Firebase.database.getReference(BOARDS)

    override suspend fun renameList(notesListItem: NotesListItem, board: Board, title: String) {
        databaseNotesListReference.child(board.id).child(notesListItem.id)
            .child(PATH_TITLE).setValue(title)
        addListOfNote(notesListItem.copy(title = title))
    }

    override suspend fun createNewList(title: String, board: Board, user: User) {
        val ref = databaseNotesListReference.child(board.id).push()
        val listId = ref.key.toString()
        val item = NotesListItem(listId, title, user.id, emptyMap())
        ref.setValue(item)
        databaseBoardsReference.child(board.id)
            .child(PATH_NOTES_LIST_IDS).updateChildren(mapOf(Pair(listId, true)))
        addListOfNote(item)
        val nodes = board.listsOfNotesIds as MutableMap
        nodes[listId] = true
        boardDataSource.addBoard(boardToBoardEntityMapper.map(board.copy(listsOfNotesIds = nodes)))
    }

    override fun getNotesListsFlow(board: Board): Flow<List<NotesListItem>> {
        return notesListDataSource.getListsOfNotesFlow(board.id).map { list ->
            list.filter {
                it.id in board.listsOfNotesIds
            }.map {
                notesListEntityToNotesListItemMapper.map(it)
            }
        }
    }

    override suspend fun addListOfNote(notesListItem: NotesListItem) {
        notesListDataSource.addListOfNotes(notesListItemToNotesListEntityMapper.map(notesListItem))
    }

    override suspend fun addAllListsOfNoteListItem(listNoteList: List<NotesListItem>) {
        notesListDataSource.addAllNotesListItems(listNoteList.map {
            notesListItemToNotesListEntityMapper.map(it)
        })
    }

    override suspend fun fetchNotesLists(
        boardId: String,
        scope: CoroutineScope,
        list: List<NotesListItem>
    ) = suspendCancellableCoroutine { continuation ->
        databaseNotesListReference.child(boardId).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listOfLists = ArrayList<NotesListItem>()
                snapshot.children.forEach {
                    val listItem = it.getValue(NotesListItem::class.java)
                    if (listItem != null && listItem !in listOfLists) {
                        listOfLists.add(listItem)
                    }
                }
                scope.launch(Dispatchers.IO) {
                    val result =
                        withContext(Dispatchers.IO) {
                            addAllListsOfNoteListItem(listOfLists)
                        }
                    if (continuation.isActive) {
                        continuation.resumeWith(Result.success(result))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) = Unit

        })
    }

    override suspend fun clearAllNotesLists() {
        notesListDataSource.clearAllNotesListsInRoom()
    }
}