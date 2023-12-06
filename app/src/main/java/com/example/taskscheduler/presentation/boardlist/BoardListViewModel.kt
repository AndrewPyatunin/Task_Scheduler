package com.example.taskscheduler.presentation.boardlist

import androidx.lifecycle.*
import com.example.taskscheduler.MyDatabaseConnection
import com.example.taskscheduler.data.TaskDatabase
import com.example.taskscheduler.domain.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class BoardListViewModel @Inject constructor(
    private val getBoardsFlowUseCase: GetBoardsFlowUseCase,
    private val getUserFlowFromRoomUseCase: GetUserFlowFromRoomUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {
    private val firebaseDatabase = Firebase.database
    private val databaseUsersReference = firebaseDatabase.getReference("Users")

    fun getBoardsFlow(user: User) = getBoardsFlowUseCase.execute(user).onEach {

    }.onStart {  }


    val userFlow = (auth.currentUser?.uid ?: MyDatabaseConnection.userId)?.let {
        getUserFlowFromRoomUseCase.execute(
            it
        )
    }



    private val _firebaseUser = MutableLiveData<FirebaseUser>()
    val firebaseUser: LiveData<FirebaseUser>
        get() = _firebaseUser

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    init {
        auth.addAuthStateListener {
            if (it.currentUser == null) {
                _firebaseUser.value = auth.currentUser
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


    fun logout() {
        if (user.value != null) {
            databaseUsersReference.child(user.value!!.id).child("onlineStatus").setValue(false)
        }
        auth.signOut()
    }
}