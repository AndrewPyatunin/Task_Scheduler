package com.example.taskscheduler.data

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.taskscheduler.MyDatabaseConnection
import com.example.taskscheduler.data.mappers.*
import com.example.taskscheduler.domain.*
import com.example.taskscheduler.domain.models.*
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

class TaskRepositoryImpl(
    private val localDataSource: LocalDataSource,
    private val mapperForUserAndUserDb: MapperForUserAndUserDb,
    private val mapperForBoardAndBoardDb: MapperForBoardAndBoardDb,
    private val mapperForNoteAndNoteDb: MapperForNoteAndNoteDb,
    private val mapperForListsOfNotesAndListsOfNotesDb: MapperForListsOfNotesAndListsOfNotesDb,
    private val mapperForInviteAndInviteDb: MapperForInviteAndInviteDb,
    private val mapperForUserAndUserForInvitesDb: MapperForUserAndUserForInvitesDb,
): TaskRepository {

    private val auth = Firebase.auth
    private var taskDatabase: TaskDatabase? = null
    private val firebaseDatabase = Firebase.database
    private val storageReference = Firebase.storage.reference
    private val databaseBoardsReference = firebaseDatabase.getReference("Boards")
    private val databaseUsersReference = firebaseDatabase.getReference("Users")
    private val databaseInvitesReference = firebaseDatabase.getReference("Invites")
    private val databaseListsOfNotesRef = firebaseDatabase.getReference("ListsOfNotes")
    private val databaseNotesRef = firebaseDatabase.getReference("Notes")

    private val flow = MutableSharedFlow<List<Board>>(1)
    private var list = emptyList<Board>()
    private val user = auth.currentUser


    override fun getUserFlow(userId: String): Flow<User> {
        return localDataSource.getUser(userId).map {
            mapperForUserAndUserDb.map(it)
        }
    }

    private fun getBoards(): List<Board> {
        return localDataSource.getBoards().let {
            mapperForBoardAndBoardDb.listMap(it)
        }
    }

    override fun getBoardsFlowFromRoom(): Flow<List<Board>> {
        return localDataSource.getBoardsFlow().map {
            mapperForBoardAndBoardDb.listMap(it)
        }
    }

    override fun getNotesFlow(listOfNotesItemId: String): Flow<List<Note>> {
        return localDataSource.getNotesFlow(listOfNotesItemId).map {
            mapperForNoteAndNoteDb.mapList(it)
        }
    }

    override fun getListsOfNotesFlow(board: Board): Flow<List<ListOfNotesItem>> {
        return localDataSource.getListsOfNotesFlow(board.id).map {
            mapperForListsOfNotesAndListsOfNotesDb.mapList(it)
        }
    }

    override fun getInvitesFlow(): Flow<List<Invite>> {
        return localDataSource.getInvites().map {
            mapperForInviteAndInviteDb.mapList(it)
        }
    }

    override fun getUsersForInvitesFlow(): Flow<List<User>> {
        return localDataSource.getUsersForInvites().map {
            mapperForUserAndUserForInvitesDb.mapList(it)
        }
    }

    override fun addUser(user: User) {
        localDataSource.addUser(mapperForUserAndUserDb.mapToDb(user))
    }

    override fun addUserForInvites(user: User) {
        localDataSource.addUserForInvites(mapperForUserAndUserForInvitesDb.mapToUserInvitesDb(user))
    }

    override fun addBoard(board: Board) {
        localDataSource.addBoard(mapperForBoardAndBoardDb.mapToDb(board))
    }

    override fun addListOfNote(listOfNotesItem: ListOfNotesItem) {
        localDataSource.addListOfNotes(mapperForListsOfNotesAndListsOfNotesDb.mapToDb(listOfNotesItem))
    }

    override fun addNote(note: Note) {
        localDataSource.addNote(mapperForNoteAndNoteDb.mapToDb(note))
    }

    override fun addInvite(invite: Invite) {
        localDataSource.addInvite(mapperForInviteAndInviteDb.mapToDb(invite))
    }

    private val _flowBoards = MutableSharedFlow<Board>()
    val flowBoards: SharedFlow<Board> = _flowBoards.asSharedFlow()

    override fun getBoards(user: User) {

        //сущность из персистант вебсокет клин архитекче веб сокет
        auth.addAuthStateListener {
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val boardsFromRoomDb = getBoards()
                    val boardsId = user.boards
                    val boardsFromDb = ArrayList<Board>()
                    for (dataSnapshot in snapshot.children) {
                        val board = dataSnapshot.getValue(Board::class.java)
                        if (board != null && dataSnapshot.key in boardsId && board !in boardsFromRoomDb) {
//                                    boardsFromDb.add(board)

                            CoroutineScope(Dispatchers.IO).launch {
                                _flowBoards.emit(board)
                                addBoard(board)//Добавление в room
                            }

                        }
                    }

//                            for (boardDb in boardsFromDb) {
//                                if (boardDb !in boardsFromRoomDb)
//                                    addBoard(boardDb)
//                            }
//                            boardsData.value = boardsFromDb

                }

                override fun onCancelled(error: DatabaseError) {

                    // logout()
                }
            }
            if (it.currentUser != null) {
                databaseBoardsReference.addValueEventListener(listener)

            } else {
                getBoards()
            }
        }
    }

    override fun getBoardsFlow(user: User): Flow<List<Board>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val boardsFromRoomDb = getBoards()
                val boardsId = user.boards
                val boardsFromDb = ArrayList<Board>()
                for (dataSnapshot in snapshot.children) {
                    val board = dataSnapshot.getValue(Board::class.java)
                    if (board != null && dataSnapshot.key in boardsId && board !in boardsFromRoomDb) {
                        boardsFromDb.add(board)

                    }
                }
                trySend(boardsFromDb as List<Board>)

//                            for (boardDb in boardsFromDb) {
//                                if (boardDb !in boardsFromRoomDb)
//                                    addBoard(boardDb)
//                            }
//                            boardsData.value = boardsFromDb

            }

            override fun onCancelled(error: DatabaseError) {
                throw RuntimeException(error.message)
                // logout()
            }
        }
        auth.addAuthStateListener {
            if (it.currentUser != null) {
                databaseBoardsReference.addValueEventListener(listener)
            } else {
                getBoards()
            }
        }
        awaitClose {
            databaseUsersReference.removeEventListener(listener)
        }
    }

    override fun getUser(userId: String) = callbackFlow<User> {
        val queryForUser = databaseUsersReference.child(auth.currentUser?.uid ?: "")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userFromDb = snapshot.getValue(User::class.java) as User
                trySend(userFromDb)
//                scope.async {
//                    addUser(userFromDb)
//                }
                // _user.postValue(userFromDb as User)
            }

            override fun onCancelled(error: DatabaseError) {
                throw RuntimeException(error.message)
                // logout()
            }
        }
        queryForUser.addValueEventListener(listener)
        awaitClose {
            queryForUser.removeEventListener(listener)
        }
    }

    override suspend fun getUsersForInvites(currentUser: User, board: Board, scope: CoroutineScope) {
        val listUsers = MutableSharedFlow<List<User>>()
        databaseUsersReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val usersFromDb = ArrayList<User>()
                for (dataSnapshot in snapshot.children) {
                    val user = dataSnapshot.getValue(User::class.java)
                    if (user != null && user.id != auth.currentUser?.uid &&
                        user.id !in board.members && board.id !in user.invites.keys) {
                        usersFromDb.add(user)
                        scope.launch {
                            addUserForInvites(user)// кладем данные в room
                            //нужно вернуть результат в случае успеха
                        }

                    }
                }
//                CoroutineScope(Dispatchers.IO).launch {
//                    listUsers.emit(usersFromDb)
//                }
            }

            override fun onCancelled(error: DatabaseError) {
                auth.signOut()
            }

        })
    }

    override fun getBoard() {
        TODO("Not yet implemented")
    }

    override fun createNewBoard(name: String, user: User, urlBackground: String, board: Board, scope: CoroutineScope) {
//        val _boardLiveData = MutableLiveData<Board>()
//        val _user = MutableLiveData<User>()
//        val mediatorLiveData = MediatorLiveData<Pair<Board, User>>()
        val urlForBoard = databaseBoardsReference.push()
        val idBoard = urlForBoard.key ?: ""
        if (board.id != "") {
            Log.i("USER_CREATE", board.id)
            val ref = databaseBoardsReference.child(board.id)
            ref.child("backgroundUrl").setValue(urlBackground)
            ref.child("title").setValue(name)
            val boardDb = board.copy(title = name, backgroundUrl = urlBackground)
//            addUser(user)
            scope.launch {
                addBoard(boardDb)
            }

//            _user.value = user
//            _boardLiveData.value = board
        } else {
            databaseUsersReference.child(user.id)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        databaseUsersReference.removeEventListener(this)
                        val listBoardsIds = ArrayList<String>()
                        if (snapshot.child("boards").value == null) {
                            listBoardsIds.add(idBoard)
                        } else {
                            for (dataSnapshot in snapshot.child("boards")
                                .children) {
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
                        val userToDb = user.copy(
                            onlineStatus = true,
                            boards = listBoardsIds
                        )
                        databaseUsersReference.child(user.id).child("boards")
                            .setValue(listBoardsIds)
                        addUser(userToDb)
                        addBoard(boardToDb)
//                        _user.value = userToDb
//                        _boardLiveData.value = board
                    }

                    override fun onCancelled(error: DatabaseError) {
//                        _error.value = error.message
                        logout()
                    }
                })
        }
//        mediatorLiveData.addSource(_boardLiveData) {
//            val userData = _user.value
//            val boardData = _boardLiveData.value
//            if (userData != null && boardData != null) {
//                mediatorLiveData.value = Pair(boardData, userData)
//            }
//        }
//        return mediatorLiveData
    }
    private val flowBoardUpdate = MutableSharedFlow<String>()
    fun flowBoardUpdate() = flowBoardUpdate.asSharedFlow()

    override fun updateBoard(board: Board, listOfNotesItemId: String, scope: CoroutineScope): Flow<String> {
        val listOfNotesIdsNew = board.listsOfNotesIds
        (listOfNotesIdsNew as ArrayList).remove(listOfNotesItemId)
        databaseBoardsReference.child(board.id).child("listsOfNotesIds").setValue(listOfNotesIdsNew).addOnSuccessListener {
            scope.launch {
                flowBoardUpdate.emit("Update of data was successful")
            }

        }.addOnFailureListener {
            it.message?.let { it1 ->
                scope.launch {
                    flowBoardUpdate.emit(it1)
                }
            }
        }
        scope.launch {
            addBoard(board.copy(listsOfNotesIds = listOfNotesIdsNew))//Добавление в Room
        }
    }

    override fun deleteBoard(board: Board, user: User) {
        databaseBoardsReference.child(board.id).removeValue()
        val listBoardsIds = user.boards as ArrayList<String>
        listBoardsIds.remove(board.id)
        databaseUsersReference.child(user.id).child("boards").setValue(listBoardsIds)
        val listsOfNotes = getListsOfNotes()
        val listsFromBoard = listsOfNotes.filter { it.id in board.listsOfNotesIds }
        for (list in listsFromBoard)
            deleteList(list, board, false)
    }

    private fun getListsOfNotes(): List<ListOfNotesItem> {
        return localDataSource.getListsOfNotes().map { mapperForListsOfNotesAndListsOfNotesDb.map(it) }
    }

    override fun renameList(listOfNotesItem: ListOfNotesItem, board: Board, title: String) {
        databaseListsOfNotesRef.child(board.id).child(listOfNotesItem.id)
            .child("title").setValue(title)
        addListOfNote(listOfNotesItem.copy(title = title))
    }

    override fun deleteList(listOfNotesItem: ListOfNotesItem, board: Board, isList: Boolean) {
        databaseListsOfNotesRef.child(board.id).child(listOfNotesItem.id).removeValue()
        localDataSource.removeListOfNotes(listOfNotesItem.id)
        val listNotes = localDataSource.getNotes().filter { it.id in listOfNotesItem.listNotes.keys }
        if (listNotes != null) {
            for (note in listNotes) {
                databaseNotesRef.child(note.id).removeValue()
                localDataSource.removeNote(note)
            }
        }
        if (isList) updateBoard(board, listOfNotesItem.id)
    }

    private fun readData(boardId: String) {
        databaseListsOfNotesRef.child(boardId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listOfNotesItem = ArrayList<ListOfNotesItem>()
                for (dataSnapshot in snapshot.children) {
                    val list = dataSnapshot.getValue(ListOfNotesItem::class.java)
                    if (list != null) listOfNotesItem.add(list)
                }
                Log.i("USER_LIST_OF_NOTES", listOfNotesItem.size.toString())
                _listLiveData.value = listOfNotesItem
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun createNewList(title: String, board: Board, user: User): LiveData<Board> {
        val listOfNotesIds = ArrayList<String>()
        val ref = databaseListsOfNotesRef.child(board.id).push()
        val listId = ref.key.toString()
        readData(board.id)
        val item = ListOfNotesItem(listId, title, user.id, emptyMap())
        ref.setValue(item)
        databaseBoardsReference.child(board.id)
            .addListenerForSingleValueEvent(object :
                ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    databaseBoardsReference.removeEventListener(this)
                    if (snapshot.hasChild("listsOfNotesIds")) {
                        for (dataSnapshot in snapshot
                            .child("listsOfNotesIds").children) {
                            listOfNotesIds.add(dataSnapshot.value.toString())
                        }
                    }
                    listOfNotesIds.add(listId)
                    databaseBoardsReference.child(board.id)
                        .child("listsOfNotesIds").setValue(listOfNotesIds)
                    board.listsOfNotesIds = listOfNotesIds
                    _boardLiveData.value = board
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        return item
    }

    override fun getNotes(listNotesIds: List<String>): Flow<List<Note>> {
        val flowNotes = MutableSharedFlow<List<Note>>()

        databaseNotesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listNotes = ArrayList<Note>()
                for (dataSnapshot in snapshot.children) {
                    if (dataSnapshot.key in listNotesIds) {
                        val note = dataSnapshot.getValue(Note::class.java)
                        note?.let { listNotes.add(it) }
                        note?.let { addNote(it) }

                    }
                }
                listNotes.let { CoroutineScope(Dispatchers.IO).launch {
                    flowNotes.emit(it)
                } }
//                _listNotesLiveData.value = listNotes
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
        return flowNotes.asSharedFlow()
    }

    override fun createNewNote(
        title: String,
        description: String,
        board: Board,
        listOfNotesItem: ListOfNotesItem,
        user: User,
        scope: CoroutineScope,
        checkList: List<CheckNoteItem>
    ): LiveData<Board> {
        val childListNotesRef = databaseListsOfNotesRef
            .child(board.id).child(listOfNotesItem.id).child("listNotes")
        val url = childListNotesRef.push()
        val idNote = url.key ?: ""
        val listNotes = listOfNotesItem.listNotes

        val note = Note(idNote, title, user.id, emptyList(), description, "", checkList)
        databaseNotesRef.child(idNote).setValue(note)
        listNotes as HashMap<String, Boolean>
        listNotes.put(idNote, true)
        listOfNotesItem.listNotes = listNotes
        scope.launch {
            addNote(note)
            addListOfNote(listOfNotesItem.copy(listNotes = listNotes))
        }
        url.setValue(true)
        MyDatabaseConnection.updated = true
        _success.postValue(board)
    }

    override fun updateNote(note: Note): LiveData<List<CheckNoteItem>> {
        databaseNotesRef.child(note.id).setValue(note)
        val checkList = ArrayList<CheckNoteItem>()
//        listNotes as HashMap<String, Boolean>
//        listNotes.put(note.id, true)
        checkList.addAll(note.listOfTasks)
        CoroutineScope(Dispatchers.IO).launch {
            addNote(note)
        }

//        _noteData.postValue(checkList)
    }

    override fun deleteNote(note: Note, board: Board, listOfNotesItem: ListOfNotesItem) {
        MyDatabaseConnection.updated = true
        databaseNotesRef.child(note.id).removeValue()
        databaseListsOfNotesRef.child(board.id).child(listOfNotesItem.id)
            .child("listNotes").child(note.id).removeValue()
        localDataSource.removeNote(mapperForNoteAndNoteDb.mapToDb(note))
        val listNotes: HashMap<String, Boolean> = listOfNotesItem.listNotes as HashMap
        listNotes.remove(note.id)
        addListOfNote(listOfNotesItem.copy(listNotes = listNotes))
    }

    override fun moveNote(listOfNotesItem: ListOfNotesItem, note: Note, board: Board, user: User) {
        MyDatabaseConnection.updated = true
        deleteNote(note, board, listOfNotesItem)
        createNewNote(note.title, note.description, board, listOfNotesItem, user, note.listOfTasks)
    }

    override fun getListOfListNotes(boardId: String): LiveData<List<ListOfNotesItem>> {
        TODO("Not yet implemented")
    }

    override fun signUp(
        email: String,
        password: String,
        name: String,
        lastName: String,
        uri: Uri?
    ): LiveData<User> {
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            val userId = it.user?.uid ?: return@addOnSuccessListener
            if (uri != null) {
                uploadUserAvatar(uri, "$name $lastName", object : TaskRepository.UrlCallback {
                    override fun onUrlCallback(url: String) {
                        val user = User(userId, name, lastName, email, true, emptyList(), url)
                        addUser(user)
                        databaseUsersReference.child(userId).setValue(user)
                        _user.value = user

                    }

                })

            }

        }.addOnFailureListener {
            _error.value = it.message
        }

    }

    interface UrlCallback {
        fun onUrlCallback(url: String)
    }

    override fun updateUserAvatar(uri: Uri, name: String): LiveData<FirebaseUser> {
        val user = auth.currentUser
        val profileUpdates = userProfileChangeRequest {
            photoUri = uri
            displayName = name
        }


        user?.updateProfile(profileUpdates)
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(
                        getApplication(),
                        "Обновление данных пользователя прошло успешно",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.i("USER_FIREBASE_SUCCESS", auth.currentUser.toString())
                    _success.value = auth.currentUser
                }
            }
    }

    override fun uploadUserAvatar(
        uri: Uri,
        name: String,
        callback: TaskRepository.UrlCallback
    ) {
        val imageRef = storageReference.child("images/${uri.lastPathSegment}")
        imageRef.putFile(uri).continueWithTask {
            if (!it.isSuccessful) {
                it.exception?.let { exception ->
                    throw exception
                }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener {
            Log.i("USER_URL", it.result.toString())
            if (it.isSuccessful) {
                updateUserAvatar(it.result, name)
                val urlToFile = it.result.toString()
                callback.onUrlCallback(urlToFile)
            } else {
                Toast.makeText(getApplication(), "${it.result}", Toast.LENGTH_SHORT).show()
            }
        }
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

    override fun update(uri: Uri?, name: String) {
        if (uri != null) {
            uploadUserAvatar(uri, name, object : TaskRepository.UrlCallback {
                override fun onUrlCallback(url: String) {
                    databaseUsersReference.child(user!!.uid).child("uri").setValue(url)
                    _uriLiveData.value = uri!!
                    Toast.makeText(
                        getApplication(),
                        "Обновление данных пользователя прошло успешно",
                        Toast.LENGTH_SHORT)
                        .show()
                }

            })
        }
    }

    override fun updateUserEmail(email: String, ref: DatabaseReference): LiveData<String> {
        user!!.updateEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(getApplication(), "Электронный адрес пользователя обновлен",
                        Toast.LENGTH_SHORT).show()
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
        databaseInvitesReference.child(auth.currentUser?.uid.toString()).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listInvites = ArrayList<Invite>()
                for (dataSnapshot in snapshot.children) {
                    for (data in dataSnapshot.children) {
                        data.getValue(Invite::class.java)?.let { listInvites.add(it) }
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

    override fun acceptInvite(user: User, invite: Invite) {
        val boardsList: ArrayList<String> = user.boards as ArrayList<String>
        val inviteBoardId = invite.boardId
        boardsList.add(inviteBoardId)
        databaseUsersReference.child(user.id).child("boards").setValue(boardsList)
        val listMembers = ArrayList<String>()
        databaseBoardsReference.child(inviteBoardId).child("members").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                databaseBoardsReference.removeEventListener(this)
                for (dataSnapshot in snapshot.children) {
                    listMembers.add(dataSnapshot.value.toString())
                    Log.i("USER_MEMBERS_FROM", dataSnapshot.value.toString())
                }
                listMembers.add(user.id)
                databaseBoardsReference.child(inviteBoardId).child("members").setValue(listMembers)
            }

            override fun onCancelled(error: DatabaseError) {
                logout()
            }
        })
        clearInviteInDatabase(user.id, inviteBoardId)
    }

    override fun declineInvite(user: User, invite: Invite) {
        clearInviteInDatabase(user.id, invite.boardId)
    }

    override fun clearInviteInDatabase(userId: String, inviteBoardId: String) {
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
                    if (snapshot.hasChildren()) {

                    } else {
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

                override fun onCancelled(error: DatabaseError) {

                }

            })
        return _success
    }

    override fun logout() {
        auth.signOut()
    }
}