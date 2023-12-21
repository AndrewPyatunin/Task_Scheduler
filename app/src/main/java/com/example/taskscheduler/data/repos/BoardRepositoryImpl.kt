package com.example.taskscheduler.data.repos

import android.util.Log
import com.example.taskscheduler.data.datasources.BoardDataSource
import com.example.taskscheduler.data.datasources.NotesListDataSource
import com.example.taskscheduler.data.entities.BoardEntity
import com.example.taskscheduler.data.entities.NotesListEntity
import com.example.taskscheduler.data.mappers.Mapper
import com.example.taskscheduler.domain.repos.BoardRepository
import com.example.taskscheduler.domain.repos.NotesListRepository
import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.NotesListItem
import com.example.taskscheduler.domain.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class BoardRepositoryImpl(
    private val boardDataSource: BoardDataSource,
    private val boardToBoardEntityMapper: Mapper<Board, BoardEntity>,
    private val boardEntityToBoardMapper: Mapper<BoardEntity, Board>,
    private val notesListEntityToNotesListItemMapper: Mapper<NotesListEntity, NotesListItem>,
    private val databaseBoardsReference: DatabaseReference,
    private val databaseUsersReference: DatabaseReference,
    private val notesListDataSource: NotesListDataSource,
    private val notesListRepository: NotesListRepository,
    private val auth: FirebaseAuth
) : BoardRepository {

    override fun getBoardsFlow(user: User): Flow<List<Board>> = callbackFlow {
        val listener = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val boardsFromDb = ArrayList<Board>()
                snapshot.children.forEach { dataSnapshot ->
                    val board = dataSnapshot.getValue(Board::class.java)
                    if (board != null && dataSnapshot.key in user.boards) {
                        boardsFromDb.add(board)
                    }
                }
                trySend(boardsFromDb as List<Board>)
            }

            override fun onCancelled(error: DatabaseError) {
                throw RuntimeException(error.message)
            }
        }
        auth.addAuthStateListener {
            if (it.currentUser != null) {
                databaseBoardsReference.addValueEventListener(listener)
            }
        }
        awaitClose {
            databaseBoardsReference.removeEventListener(listener)
        }
    }

    override fun getBoardsFlowFromRoom(user: User): Flow<List<Board>> {
        return boardDataSource.getBoardsFlow().map { list ->
            list.map {
                boardEntityToBoardMapper.map(it)
            }
        }
    }

    override suspend fun updateBoard(
        board: Board, listOfNotesItemId: String, scope: CoroutineScope
    ): String = suspendCoroutine { continuation ->
        val listOfNotesIdsNew = board.listsOfNotesIds
        (listOfNotesIdsNew as ArrayList).remove(listOfNotesItemId)
        databaseBoardsReference.child(board.id).child("listsOfNotesIds").setValue(listOfNotesIdsNew)
            .addOnSuccessListener {
                continuation.resumeWith(Result.success("Data update was successful!"))

            }.addOnFailureListener {
                continuation.resumeWith(Result.failure(it))
            }
        scope.launch {
            addBoard(board.copy(listsOfNotesIds = listOfNotesIdsNew))//Добавление в Room
        }
    }

    override suspend fun deleteBoard(board: Board, user: User) {
        databaseBoardsReference.child(board.id).removeValue()
        val listBoardsIds = user.boards as ArrayList<String>
        listBoardsIds.remove(board.id)
        databaseUsersReference.child(user.id).child("boards").setValue(listBoardsIds)
        val listsOfNotes = notesListDataSource.getListsOfNotes().map {
            notesListEntityToNotesListItemMapper.map(it)
        }
        listsOfNotes.filter {
            it.id in board.listsOfNotesIds
        }.forEach {
            notesListRepository.deleteList(it, board, false)
        }
    }

    override suspend fun addBoard(board: Board) {
        boardDataSource.addBoard(boardToBoardEntityMapper.map(board))
    }

    override suspend fun createNewBoard(
        name: String, user: User, urlBackground: String, board: Board
    ): Board = suspendCoroutine { continuation ->

        val urlForBoard = databaseBoardsReference.push()
        val idBoard = urlForBoard.key ?: ""
        if (board.id != "") {
            Log.i("USER_CREATE", board.id)
            val ref = databaseBoardsReference.child(board.id)
            ref.child("backgroundUrl").setValue(urlBackground)
            ref.child("title").setValue(name)
            val boardDb = board.copy(title = name, backgroundUrl = urlBackground)
            continuation.resume(boardDb)
        } else {
            val usersRef = databaseUsersReference.child(user.id)

            usersRef.addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    databaseUsersReference.removeEventListener(this)
                    val listBoardsIds = ArrayList<String>()
                    snapshot.child("boards").children.forEach {
                        val data = it.value as String
                        listBoardsIds.add(data)
                    }
                    listBoardsIds.add(idBoard)
                    val boardToDb = Board(
                        idBoard, name, user.id, urlBackground, listOf(user.id), emptyList()
                    )
                    urlForBoard.setValue(boardToDb)
                    databaseUsersReference.child(user.id).child("boards").setValue(listBoardsIds)
                    continuation.resumeWith(Result.success(boardToDb))
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(error.toException())
                    throw RuntimeException(error.message)
                }
            })
        }
    }
}