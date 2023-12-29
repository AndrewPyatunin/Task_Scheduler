package com.example.taskscheduler.data.repos

import android.net.Uri
import com.example.taskscheduler.data.FirebaseConstants.USERS
import com.example.taskscheduler.data.TaskDatabaseDao
import com.example.taskscheduler.data.datasources.UserDataSourceImpl
import com.example.taskscheduler.data.entities.UserEntity
import com.example.taskscheduler.data.mappers.UserEntityToUserMapper
import com.example.taskscheduler.data.mappers.UserToUserEntityMapper
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.domain.repos.UserRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.coroutines.suspendCoroutine

class UserRepositoryImpl(
    dao: TaskDatabaseDao
) : UserRepository {

    private val userDataSource = UserDataSourceImpl(dao)
    private val userToUserEntityMapper = UserToUserEntityMapper()
    private val userEntityToUserMapper = UserEntityToUserMapper()
    private val auth = Firebase.auth
    private val userAuth = UserAuthentication(dao)
    private val databaseUsersReference = Firebase.database.getReference(USERS)

    override suspend fun addUser(user: User) {
        userDataSource.addUser(userToUserEntityMapper.map(user))
    }

    override suspend fun getUser(userId: String): User {
        return userEntityToUserMapper.map(userDataSource.getUser(userId))
    }

    override suspend fun updateUserProfile(description: String, email: String, user: User) {
        val ref = auth.currentUser?.let {
            databaseUsersReference.child(it.uid)
        }
        var userUpdated: User = user
        if (description != "") {
            ref?.child("description")?.setValue(description)
            userUpdated = user.copy(description = description)
//            it.resumeWith(Result.success(description))
        }
        if (email != "" && ref != null) {
            updateUserEmail(email, ref)
            userUpdated = user.copy(email = email)
        }
        addUser(userUpdated)
    }

    override suspend fun update(uri: Uri?, name: String, user: User) {
        uri?.let {
            userAuth.uploadUserAvatar(uri, name).onSuccess {
                auth.currentUser?.uid?.let { userId ->
                    databaseUsersReference.child(userId).child("uri").setValue(it)
                    addUser(user.copy(uri = it))
                }
            }
        }
    }

    override suspend fun updateUserEmail(email: String, ref: DatabaseReference): String =
        suspendCoroutine {
            auth.currentUser?.updateEmail(email)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        ref.child("email").setValue(email)
                        it.resumeWith(Result.success(email))
                    }
                }
        }

    override fun updateStatus() {
        auth.addAuthStateListener {
            it.currentUser?.let {
                databaseUsersReference.child(it.uid).child("onlineStatus").setValue(true)
            }
        }
    }

    override fun getUsers(): Flow<List<User>> {
        return userDataSource.getUsers().map { list ->
            list.map {
                userEntityToUserMapper.map(it)
            }
        }
    }

    override suspend fun addAllUsers(scope: CoroutineScope) {
        databaseUsersReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = ArrayList<User>()
                snapshot.children.forEach {
                    it.getValue(User::class.java)?.let { it1 -> users.add(it1) }
                }
                scope.launch(Dispatchers.IO) {
                    userDataSource.addAllUsers(users.map {
                        userToUserEntityMapper.map(it)
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) = Unit

        })
    }

    override suspend fun clearAllUsers() {
        userDataSource.clearAllUsersInRoom()
    }
}