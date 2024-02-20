package com.example.taskscheduler.data.repos

import com.example.taskscheduler.data.FirebaseConstants.BOARDS
import com.example.taskscheduler.data.FirebaseConstants.NOTES
import com.example.taskscheduler.data.FirebaseConstants.NOTES_LIST
import com.example.taskscheduler.data.FirebaseConstants.PATH_BACKGROUND_URL
import com.example.taskscheduler.data.FirebaseConstants.PATH_BOARDS
import com.example.taskscheduler.data.FirebaseConstants.PATH_NOTES_LIST_IDS
import com.example.taskscheduler.data.FirebaseConstants.PATH_TITLE
import com.example.taskscheduler.data.FirebaseConstants.USERS
import com.example.taskscheduler.data.datasources.*
import com.example.taskscheduler.data.entities.BoardEntity
import com.example.taskscheduler.data.entities.NotesListEntity
import com.example.taskscheduler.data.entities.UserEntity
import com.example.taskscheduler.data.mappers.*
import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.NotesListItem
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.domain.repos.BoardRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject

class BoardRepositoryImpl @Inject constructor(
    private val boardDataSource: BoardDataSource,
    private val noteDataSource: NoteDataSource,
    private val userDataSource: UserDataSource,
    private val notesListDataSource: NotesListDataSource,
    private val boardToBoardEntityMapper: Mapper<Board, BoardEntity>,
    private val boardEntityToBoardMapper: Mapper<BoardEntity?, Board>,
    private val userToUserEntityMapper: Mapper<User, UserEntity>,
    private val notesListEntityToNotesListItemMapper: Mapper<NotesListEntity?, NotesListItem>,
) : BoardRepository {

    private val database = Firebase.database
    private val databaseNotesListReference = database.getReference(NOTES_LIST)
    private val databaseNoteReference = database.getReference(NOTES)
    private val databaseBoardsReference = database.getReference(BOARDS)
    private val databaseUsersReference = database.getReference(USERS)

    override suspend fun getBoardsFlow(user: User, scope: CoroutineScope, boardList: List<Board>) =
        suspendCancellableCoroutine { continuation ->
            databaseBoardsReference.addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val boards = ArrayList<Board>()
                    snapshot.children.forEach { dataSnapshot ->
                        val board = dataSnapshot.getValue(Board::class.java)
                        if (board != null && dataSnapshot.key in user.boards) { //&& board !in boardList) {
                            boards.add(board)
                        }
                    }
                    scope.launch(Dispatchers.IO) {
                        val result = addBoards(boards)
                        if (continuation.isActive)
                            continuation.resumeWith(Result.success(result))//add to room database
                    }
                }

                override fun onCancelled(error: DatabaseError) = Unit
            })
        }

    override fun getBoardsFlowFromRoom(user: User): Flow<List<Board>> {
        return boardDataSource.getBoardsFlow().map { list ->
            list.map(boardEntityToBoardMapper::map)
        }
    }

    override fun getBoard(boardId: String): Flow<Board> {
        return boardDataSource.getBoard(boardId).map { boardEntityToBoardMapper.map(it) }
    }

    override suspend fun updateBoard(
        board: Board, notesListItem: NotesListItem
    ) {
        val nodes = board.listsOfNotesIds.filter { it.key != notesListItem.id }
        databaseBoardsReference.child(board.id).child(PATH_NOTES_LIST_IDS).child(notesListItem.id)
            .setValue(null)
        addBoard(board.copy(listsOfNotesIds = nodes))//Добавление в Room
    }

    override suspend fun deleteBoard(board: Board, user: User) {
        databaseBoardsReference.child(board.id).removeValue()
        board.members.keys.forEach {
            databaseUsersReference.child(it).child(PATH_BOARDS).child(board.id).removeValue()
        }

        val listsOfNotes = notesListDataSource.getListsOfNotes().map {
            notesListEntityToNotesListItemMapper.map(it)
        }
        boardDataSource.removeBoard(boardToBoardEntityMapper.map(board))

        listsOfNotes.filter {
            it.id in board.listsOfNotesIds
        }.forEach {
            deleteList(
                notesListItem = it,
                board = board,
                isList = false
            )
        }
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
        if (isList) updateBoard(board, notesListItem)
    }

    override suspend fun clearAllBoards() {
        boardDataSource.clearAllBoardsInRoom()
    }

    override suspend fun addBoard(board: Board) {
        boardDataSource.addBoard(boardToBoardEntityMapper.map(board))
    }

    override suspend fun addBoards(boardList: List<Board>) {
        boardDataSource.addBoards(boardList.map(boardToBoardEntityMapper::map))
    }

    override suspend fun createNewBoard(
        name: String, user: User, urlBackground: String, board: Board
    ): Result<String> {
        val boardDb: Board
        val urlForBoard = databaseBoardsReference.push()
        val idBoard =
            urlForBoard.key ?: return Result.failure(RuntimeException("No Internet connection!"))
        if (board.id.isNotEmpty()) {
            val ref = databaseBoardsReference.child(board.id)
            ref.child(PATH_BACKGROUND_URL).setValue(urlBackground)
            ref.child(PATH_TITLE).setValue(name)
            boardDb = board.copy(title = name, backgroundUrl = urlBackground)
        } else {
            boardDb = Board(idBoard, name, user.id, urlBackground, mapOf(Pair(user.id, true)))
            urlForBoard.setValue(boardDb)
            databaseUsersReference.child(user.id).child(PATH_BOARDS)
                .updateChildren(mapOf(Pair(idBoard, true)))
        }
        addBoard(boardDb)
        userDataSource.addUser(
            userToUserEntityMapper.map(
                user.copy(
                    boards = (user.boards.plus(
                        idBoard to true
                    ))
                )
            )
        )
        return Result.success(idBoard)
    }
}