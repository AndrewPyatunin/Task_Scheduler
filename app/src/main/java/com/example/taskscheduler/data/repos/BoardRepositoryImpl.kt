package com.example.taskscheduler.data.repos

import android.util.Log
import com.example.taskscheduler.data.FirebaseConstants.BOARDS
import com.example.taskscheduler.data.FirebaseConstants.USERS
import com.example.taskscheduler.data.datasources.BoardDataSource
import com.example.taskscheduler.data.datasources.NotesListDataSource
import com.example.taskscheduler.data.entities.BoardEntity
import com.example.taskscheduler.data.entities.NotesListEntity
import com.example.taskscheduler.data.mappers.Mapper
import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.NotesListItem
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.domain.repos.BoardRepository
import com.example.taskscheduler.domain.repos.NotesListRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class BoardRepositoryImpl(
    private val boardDataSource: BoardDataSource,
    private val boardToBoardEntityMapper: Mapper<Board, BoardEntity>,
    private val boardEntityToBoardMapper: Mapper<BoardEntity, Board>,
    private val notesListEntityToNotesListItemMapper: Mapper<NotesListEntity, NotesListItem>,
    private val notesListDataSource: NotesListDataSource,
    private val notesListRepository: NotesListRepository,
) : BoardRepository {

    private val auth = Firebase.auth
    private val databaseBoardsReference = Firebase.database.getReference(BOARDS)
    private val databaseUsersReference = Firebase.database.getReference(USERS)

    override suspend fun getBoardsFlow(user: User, scope: CoroutineScope) {
        auth.addAuthStateListener {
            if (it.currentUser != null) {
                databaseBoardsReference.addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {
                        scope.launch {
                            snapshot.children.forEach { dataSnapshot ->
                                val board = dataSnapshot.getValue(Board::class.java)
                                if (board != null && dataSnapshot.key in user.boards) {
                                    addBoard(board)
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        throw RuntimeException(error.message)
                    }
                })
            }
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
        board: Board, listOfNotesItemId: String
    ) {
        val nodes = board.listsOfNotesIds.filter { it.key != listOfNotesItemId }
        databaseBoardsReference.child(board.id).child("listsOfNotesIds").setValue(nodes)
        addBoard(board.copy(listsOfNotesIds = nodes))//Добавление в Room
    }

    override suspend fun deleteBoard(board: Board, user: User) {
        databaseBoardsReference.child(board.id).removeValue()
        databaseUsersReference.child(user.id).child("boards").setValue(user.boards.filter {
            it != user.id
        })
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
    ) {
        val boardDb: Board
        val urlForBoard = databaseBoardsReference.push()
        val idBoard = urlForBoard.key ?: ""
        if (board.id != "") {
            Log.i("USER_CREATE", board.id)
            val ref = databaseBoardsReference.child(board.id)
            ref.child("backgroundUrl").setValue(urlBackground)
            ref.child("title").setValue(name)
            boardDb = board.copy(title = name, backgroundUrl = urlBackground)
        } else {
            val usersRef = databaseUsersReference.child(user.id)
            val members = mapOf(Pair(user.id, true))
            boardDb = Board(idBoard, name, user.id, urlBackground, members)
            urlForBoard.setValue(boardDb)
            usersRef.child("boards").updateChildren(members)
        }
        addBoard(boardDb)
    }
}