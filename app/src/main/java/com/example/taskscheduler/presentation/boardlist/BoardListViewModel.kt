package com.example.taskscheduler.presentation.boardlist

import androidx.lifecycle.*
import com.example.taskscheduler.MyDatabaseConnection
import com.example.taskscheduler.data.TaskDatabase
import com.example.taskscheduler.data.TaskRepositoryImpl
import com.example.taskscheduler.domain.*
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class BoardListViewModel(
    user: User,
    private val getBoardsFlowUseCase: GetBoardsFlowUseCase
) : ViewModel() {
    private val auth = Firebase.auth
    private var taskDatabase: TaskDatabase? = null
    private val firebaseDatabase = Firebase.database
    private val databaseBoardsReference = firebaseDatabase.getReference("Boards")
    private val databaseUsersReference = firebaseDatabase.getReference("Users")
    lateinit var repository: TaskRepository
    val getBoardsFlow = getBoardsFlowUseCase.execute(user).onEach {

    }.onStart {  }

//    val repository = TaskRepositoryImpl()


    private val _userLiveData by lazy {
        repository.getUser(user.id)
    }

    private val _boardsLiveData by lazy {
        repository.getBoards(user)
    }
    val boardsLiveData: LiveData<List<Board>> = _boardsLiveData
    val userLiveData: LiveData<User> = _userLiveData


    private val observer = Observer<List<Board>> {
        _boardsLiveData.value = it
    }
    private val userObserver = Observer<User> {
        _userLiveData.value = it
    }

    override fun onCleared() {
        _boardsLiveData.removeObserver(observer)
        _userLiveData.removeObserver(userObserver)
        super.onCleared()
    }

    val _userData by lazy {
        repository.getUserFlow(user).asLiveData()
    }
    val _boardData by lazy {
        repository.getBoardsFlow().asLiveData()
    }

    private val _firebaseUser = MutableLiveData<FirebaseUser>()
    val firebaseUser: LiveData<FirebaseUser>
        get() = _firebaseUser

    private val _boardList = MutableLiveData<List<Board>>()
    val boardList: LiveData<List<Board>>
        get() = _boardList

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    init {
        auth.addAuthStateListener {
            if (it.currentUser == null) {
                _firebaseUser.value = auth.currentUser
            }
        }
        _userLiveData.observeForever(userObserver)
        _boardsLiveData.observeForever(observer)
        auth.addAuthStateListener {
            if (it.currentUser != null) {
                databaseBoardsReference.addValueEventListener(
                    object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            readData(object : MyCallback {
                                override fun onCallback(user: User) {
                                    val boardsId = user.boards
                                    val boardsFromDb = ArrayList<Board>()
                                    for (dataSnapshot in snapshot.children) {
                                        val board = dataSnapshot.getValue(Board::class.java)
                                        if (board != null && dataSnapshot.key in boardsId) {
                                            boardsFromDb.add(board)
                                        }
                                    }
                                    _boardList.value = boardsFromDb
                                }
                            })
                        }

                        override fun onCancelled(error: DatabaseError) {
                            logout()
                        }
                    })

            } else {
                val user = taskDatabase?.taskDatabaseDao()?.getUser(MyDatabaseConnection.userId ?: 0)
                val boards = taskDatabase?.taskDatabaseDao()?.getBoardsFlow() ?: emptyList()
                val list = ArrayList<Board>()
                for (board in boards) {
                    if (board.id in (user?.boards ?: emptyList())) {
                        list.add(board)
                    }
                }
                _boardList.value = list
            }
        }
    }

    fun method(): Flow<String> = callbackFlow {
        val queryForUser = databaseUsersReference.child(auth.currentUser?.uid ?: "")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userFromDb = snapshot.getValue(User::class.java)
                _user.postValue(userFromDb as User)
                trySend(userFromDb.name)
//                callback.onCallback(userFromDb)
            }

            override fun onCancelled(error: DatabaseError) {
                logout()
            }
        }
        queryForUser.addValueEventListener(listener)
        awaitClose {
            queryForUser.removeEventListener(listener)
        }
    }

    fun readData(callback: MyCallback) {
        viewModelScope.launch(Dispatchers.IO) {
            val queryForUser = databaseUsersReference.child(auth.currentUser?.uid ?: "")
            queryForUser.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userFromDb = snapshot.getValue(User::class.java)
                    _user.postValue(userFromDb as User)
                    callback.onCallback(userFromDb)
                }

                override fun onCancelled(error: DatabaseError) {
                    logout()
                }
            })
        }

    }

    interface MyCallback {
        fun onCallback(user: User)
    }

    fun logout() {
        if (user.value != null) {
            databaseUsersReference.child(user.value!!.id).child("onlineStatus").setValue(false)
        }
        auth.signOut()
    }
}