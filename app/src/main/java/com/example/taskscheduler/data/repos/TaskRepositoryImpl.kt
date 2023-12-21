package com.example.taskscheduler.data.repos

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.taskscheduler.MyDatabaseConnection
import com.example.taskscheduler.data.LocalDataSource
import com.example.taskscheduler.data.entities.*
import com.example.taskscheduler.data.mappers.*
import com.example.taskscheduler.domain.*
import com.example.taskscheduler.domain.models.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class TaskRepositoryImpl(
    private val localDataSource: LocalDataSource,
    private val userToUserEntityMapper: Mapper<User, UserEntity>,
    private val userEntityToUserMapper: Mapper<UserEntity, User>,
    private val boardToBoardEntityMapper: Mapper<Board, BoardEntity>,
    private val boardEntityToBoardMapper: Mapper<BoardEntity, Board>,
    private val noteToNoteEntityMapper: Mapper<Note, NoteEntity>,
    private val noteEntityToNoteMapper: Mapper<NoteEntity, Note>,
    private val notesListEntityToNotesListItemMapper: Mapper<NotesListEntity, NotesListItem>,
    private val notesListItemToNotesListEntityMapper: Mapper<NotesListItem, NotesListEntity>,
    private val inviteEntityToInviteMapper: Mapper<InviteEntity, Invite>,
    private val inviteToInviteEntityMapper: Mapper<Invite, InviteEntity>,
    private val userToUserForInvitesMapper: Mapper<User, UserForInvitesEntity>,
    private val userForInvitesToUserMapper: Mapper<UserForInvitesEntity, User>
) : TaskRepository {

    companion object {

        private const val BOARDS = "Boards"
        private const val USERS = "Users"
        private const val LIST_OF_NOTES = "ListOfNotes"
        private const val INVITES = "Invites"
        private const val NOTES = "Notes"
    }

    private val auth = Firebase.auth
    private val firebaseDatabase = Firebase.database
    private val databaseBoardsReference = firebaseDatabase.getReference(BOARDS)
    private val databaseUsersReference = firebaseDatabase.getReference(USERS)
    private val databaseInvitesReference = firebaseDatabase.getReference(INVITES)
    private val databaseListsOfNotesRef = firebaseDatabase.getReference(LIST_OF_NOTES)
    private val databaseNotesRef = firebaseDatabase.getReference(NOTES)
    private val user = auth.currentUser


    override fun getUserFlow(userId: String): Flow<User> {
        return localDataSource.getUser(userId).map { userEntity ->
            userEntityToUserMapper.map(userEntity)
        }
    }


    override fun getBoardsFlowFromRoom(): Flow<List<Board>> {
        return localDataSource.getBoardsFlow().map {
            it.map { boardEntity ->
                boardEntityToBoardMapper.map(boardEntity)
            }
        }
    }

    override fun getNotesFlow(listOfNotesItemId: String): Flow<List<Note>> {
        return localDataSource.getNotesFlow(listOfNotesItemId).map { list ->
            list.map { noteEntity ->
                noteEntityToNoteMapper.map(noteEntity)
            }
        }
    }

    override fun getListsOfNotesFlow(board: Board): Flow<List<NotesListItem>> {
        return localDataSource.getListsOfNotesFlow(board.id).map { list ->
            list.map { listOfNotesEntity ->
                notesListEntityToNotesListItemMapper.map(listOfNotesEntity)
            }
        }
    }

    override fun getInvitesFlow(): Flow<List<Invite>> {
        return localDataSource.getInvites().map {
            it.map { inviteEntity ->
                inviteEntityToInviteMapper.map(inviteEntity)
            }
        }
    }

    override fun getUsersForInvitesFlow(): Flow<List<User>> {
        return localDataSource.getUsersForInvites().map { list ->
            list.map {
                userForInvitesToUserMapper.map(it)
            }
        }
    }

    override suspend fun addUser(user: User) {
        localDataSource.addUser(userToUserEntityMapper.map(user))
    }

    override suspend fun addUserForInvites(user: User) {
        localDataSource.addUserForInvites(userToUserForInvitesMapper.map(user))
    }

    override suspend fun addBoard(board: Board) {
        localDataSource.addBoard(boardToBoardEntityMapper.map(board))
    }

    override suspend fun addListOfNote(notesListItem: NotesListItem) {
        localDataSource.addListOfNotes(notesListItemToNotesListEntityMapper.map(notesListItem))
    }

    override suspend fun addNote(note: Note) {
        localDataSource.addNote(noteToNoteEntityMapper.map(note))
    }

    override suspend fun addInvite(invite: Invite) {
        localDataSource.addInvite(inviteToInviteEntityMapper.map(invite))
    }

    override fun getBoardsFlow(user: User): Flow<List<Board>> = callbackFlow {
        val listener = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val boardsId = user.boards
                val boardsFromDb = ArrayList<Board>()
                snapshot.children.forEach { dataSnapshot ->
                    val board = dataSnapshot.getValue(Board::class.java)
                    if (board != null && dataSnapshot.key in boardsId) {
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
            databaseUsersReference.removeEventListener(listener)
        }
    }

    override fun getUser(userId: String) = callbackFlow<User> {
        val queryForUser = databaseUsersReference.child(userId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userFromDb = snapshot.getValue(User::class.java) as User
                trySend(userFromDb)
            }

            override fun onCancelled(error: DatabaseError) {
                throw RuntimeException(error.message)
            }
        }
        queryForUser.addValueEventListener(listener)
        awaitClose {
            queryForUser.removeEventListener(listener)
        }
    }

    override suspend fun getUsersForInvites(
        currentUser: User,
        board: Board,
        scope: CoroutineScope
    ) {
        databaseUsersReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val usersFromDb = ArrayList<User>()
                snapshot.children.forEach { dataSnapshot ->
                    val user = dataSnapshot.getValue(User::class.java)
                    if (user != null && user.id != auth.currentUser?.uid &&
                        user.id !in board.members && board.id !in user.invites.keys
                    ) {
                        usersFromDb.add(user)
                        scope.launch {
                            addUserForInvites(user)// кладем данные в room
                            //нужно вернуть результат в случае успеха
                        }

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                auth.signOut()
            }

        })
    }

    override fun getBoard() {
        TODO("Not yet implemented")
    }

    override suspend fun createNewBoard(name: String, user: User, urlBackground: String, board: Board): Board =
        suspendCoroutine { continuation ->
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
                        if (snapshot.child("boards").value == null) {
                            listBoardsIds.add(idBoard)
                        } else {
                            snapshot.child("boards").children.forEach { dataSnapshot ->
                                val data = dataSnapshot.value as String
                                listBoardsIds.add(data)
                            }
                            listBoardsIds.add(idBoard)
                        }
                        val listMembersIds = ArrayList<String>()
                        listMembersIds.add(user.id)
                        val boardToDb = Board(
                            idBoard,
                            name,
                            user.id,
                            urlBackground,
                            listMembersIds,
                            emptyList()
                        )
                        urlForBoard.setValue(boardToDb)
                        databaseUsersReference.child(user.id).child("boards")
                            .setValue(listBoardsIds)
                        continuation.resume(boardToDb)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        throw RuntimeException(error.message)
                    }
                })
            }
        }

    private val flowBoardUpdate = MutableSharedFlow<String>()
    fun flowBoardUpdate() = flowBoardUpdate.asSharedFlow()

    override suspend fun updateBoard(
        board: Board,
        listOfNotesItemId: String,
        scope: CoroutineScope
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
        val listsOfNotes = getListsOfNotes()
        val listsFromBoard = listsOfNotes.filter {
            it.id in board.listsOfNotesIds
        }.forEach {
            deleteList(it, board, false)
        }
    }

    override suspend fun renameList(notesListItem: NotesListItem, board: Board, title: String) {
        databaseListsOfNotesRef.child(board.id).child(notesListItem.id)
            .child("title").setValue(title)
        addListOfNote(notesListItem.copy(title = title))
    }

    override suspend fun deleteList(notesListItem: NotesListItem, board: Board, isList: Boolean) {
        databaseListsOfNotesRef.child(board.id).child(notesListItem.id).removeValue()
        localDataSource.removeListOfNotes(notesListItem.id)
        val listNotes =
            localDataSource.getNotes().filter { it.id in notesListItem.listNotes.keys }
        listNotes.map {
            databaseNotesRef.child(it.id).removeValue()
            localDataSource.removeNote(it)
        }
        if (isList) updateBoard(board, notesListItem.id)
    }

    override fun createNewList(title: String, board: Board, user: User): LiveData<Board> {
        val listOfNotesIds = ArrayList<String>()
        val ref = databaseListsOfNotesRef.child(board.id).push()
        val listId = ref.key.toString()
        readData(board.id)
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
                    val board = board.copy(listsOfNotesIds = listOfNotesIds)
                    _boardLiveData.value = board
                }

                override fun onCancelled(error: DatabaseError) = Unit
            })
        return item
    }

    override fun getNotes(listNotesIds: List<String>): Flow<List<Note>> {
        val flowNotes = MutableSharedFlow<List<Note>>()

        databaseNotesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listNotes = ArrayList<Note>()
                snapshot.children.forEach { dataSnapshot ->
                    if (dataSnapshot.key in listNotesIds) {
                        val note = dataSnapshot.getValue(Note::class.java)
                        note?.let { listNotes.add(it) }
                        note?.let { addNote(it) }
                    }
                }
                listNotes.let {
                    CoroutineScope(Dispatchers.IO).launch {
                        flowNotes.emit(it)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) = Unit

        })
        return flowNotes.asSharedFlow()
    }

    override suspend fun createNewNote(
        title: String,
        description: String,
        board: Board,
        notesListItem: NotesListItem,
        user: User,
        scope: CoroutineScope,
        checkList: List<CheckNoteItem>
    ): LiveData<Board> {
        val childListNotesRef = databaseListsOfNotesRef
            .child(board.id).child(notesListItem.id).child("listNotes")
        val url = childListNotesRef.push()
        val idNote = url.key ?: ""
        val listNotes = notesListItem.listNotes

        val note = Note(idNote, title, user.id, emptyList(), description, "", checkList)
        databaseNotesRef.child(idNote).setValue(note)
        listNotes as HashMap<String, Boolean>
        listNotes.put(idNote, true)
        addNote(note)
        addListOfNote(notesListItem.copy(listNotes = listNotes))
        url.setValue(true)
        MyDatabaseConnection.updated = true
        _success.postValue(board)
    }

    override fun updateNote(note: Note): LiveData<List<CheckNoteItem>> {
        databaseNotesRef.child(note.id).setValue(note)
        val checkList = ArrayList<CheckNoteItem>()
        checkList.addAll(note.listOfTasks)
        CoroutineScope(Dispatchers.IO).launch {
            addNote(note)
        }

    }

    override suspend fun deleteNote(note: Note, board: Board, notesListItem: NotesListItem) {
        MyDatabaseConnection.updated = true
        databaseNotesRef.child(note.id).removeValue()
        databaseListsOfNotesRef.child(board.id).child(notesListItem.id)
            .child("listNotes").child(note.id).removeValue()
        localDataSource.removeNote(noteToNoteEntityMapper.map(note))
        val listNotes: HashMap<String, Boolean> = notesListItem.listNotes as HashMap
        listNotes.remove(note.id)
        addListOfNote(notesListItem.copy(listNotes = listNotes))
    }

    override suspend fun moveNote(notesListItem: NotesListItem, note: Note, board: Board, user: User) {
        MyDatabaseConnection.updated = true
        deleteNote(note, board, notesListItem)
        createNewNote(note.title, note.description, board, notesListItem, user, CoroutineScope(Dispatchers.IO), note.listOfTasks)
    }

    override fun getListOfListNotes(boardId: String): LiveData<List<NotesListItem>> {
        TODO("Not yet implemented")
    }

    override fun updateUserProfile(description: String, email: String): LiveData<String> {
        val ref = databaseUsersReference.child(user!!.uid)
        if (description != "") {
            ref.child("description").setValue(description)
            _descriptionLiveData.value = description
        }
        if (email != "") {
            updateUserEmail(email, ref)
        }
    }

    override fun updateUserEmail(email: String, ref: DatabaseReference): LiveData<String> {
        user!!.updateEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        getApplication(), "Электронный адрес пользователя обновлен",
                        Toast.LENGTH_SHORT
                    ).show()
                    ref.child("email").setValue(email)
                    _emailLiveData.value = email
                }
            }
    }

    override fun updateStatus() {
        if (user != null) {
            databaseUsersReference.child(user.uid).child("onlineStatus").setValue(false)
        }
    }

    override fun getInvites(): LiveData<List<Invite>> {
        val _invitesList = MutableLiveData<List<Invite>>()
        databaseInvitesReference.child(auth.currentUser?.uid.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val listInvites = ArrayList<Invite>()
                    snapshot.children.forEach {
                        it.children.forEach {
                            it.getValue(Invite::class.java)?.let { invite ->
                                listInvites.add(invite)
                            }
                        }
                    }
                    Log.i("USER_INVITE_LIST_SIZE", listInvites.size.toString())
                    _invitesList.value = listInvites
                }

                override fun onCancelled(error: DatabaseError) {
                    logout()
                }

            })
        return _invitesList
    }

    override suspend fun acceptInvite(user: User, invite: Invite) {
        val boardsList: ArrayList<String> = user.boards as ArrayList<String>
        val inviteBoardId = invite.boardId
        boardsList.add(inviteBoardId)
        databaseUsersReference.child(user.id).child("boards").setValue(boardsList)
        val listMembers = ArrayList<String>()
        databaseBoardsReference.child(inviteBoardId).child("members")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    databaseBoardsReference.removeEventListener(this)
                    snapshot.children.forEach { dataSnapshot ->
                        listMembers.add(dataSnapshot.value.toString())
                        Log.i("USER_MEMBERS_FROM", dataSnapshot.value.toString())
                    }
                    listMembers.add(user.id)
                    databaseBoardsReference.child(inviteBoardId).child("members")
                        .setValue(listMembers)
                }

                override fun onCancelled(error: DatabaseError) {
                    logout()
                }
            })
        clearInviteInDatabase(user.id, inviteBoardId)
    }

    override suspend fun declineInvite(user: User, invite: Invite) {
        clearInviteInDatabase(user.id, invite.boardId)
    }

    override suspend fun clearInviteInDatabase(userId: String, inviteBoardId: String) {
        databaseInvitesReference.child(userId).child(inviteBoardId).removeValue()
        databaseUsersReference.child(userId).child("invites").child(inviteBoardId).removeValue()
        localDataSource.removeInvite()
        localDataSource.addUser()
    }

    override fun inviteUser(
        userForInvite: User,
        currentUser: User,
        board: Board
    ): LiveData<String> {
        val _success = MutableLiveData<String>()
        databaseInvitesReference.child(userForInvite.id).child(board.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.hasChildren()) {
                        val pushInvite =
                            databaseInvitesReference.child(userForInvite.id).child(board.id).push()
                        val inviteId = pushInvite.key.toString()
                        pushInvite.setValue(
                            Invite(
                                inviteId,
                                board.id,
                                currentUser.id,
                                currentUser.name,
                                currentUser.lastName,
                                board.title
                            )
                        )
                        val ref = databaseUsersReference.child(userForInvite.id).child("invites")
                        val map = HashMap<String, Any>()
                        map[board.id] = true
                        ref.updateChildren(map)
                        _success.value = "Приглашение успешно отправлено"
                    }

                }

                override fun onCancelled(error: DatabaseError) = Unit

            })
        return _success
    }

    override fun logout() {
        auth.signOut()
    }

    private fun readData(boardId: String) {
        databaseListsOfNotesRef.child(boardId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val notesListItem = ArrayList<NotesListItem>()
                snapshot.children.forEach { dataSnapshot ->
                    val list = dataSnapshot.getValue(NotesListItem::class.java)
                    if (list != null) notesListItem.add(list)
                }
                Log.i("USER_LIST_OF_NOTES", notesListItem.size.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("USER_LIST_OF_NOTES", error.message)
            }

        })
    }

    private fun getListsOfNotes(): List<NotesListItem> {
        return localDataSource.getListsOfNotes()
            .map { notesListEntityToNotesListItemMapper.map(it) }
    }

    private suspend fun getBoards(): List<Board> {
        return localDataSource.getBoards().let {
            it.map { boardEntity ->
                boardEntityToBoardMapper.map(boardEntity)
            }
        }
    }
}