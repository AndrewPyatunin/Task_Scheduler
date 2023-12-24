package com.example.taskscheduler.data.repos

import android.net.Uri
import android.util.Log
import com.example.taskscheduler.data.FirebaseConstants.USERS
import com.example.taskscheduler.data.datasources.UserDataSource
import com.example.taskscheduler.data.entities.UserEntity
import com.example.taskscheduler.data.mappers.Mapper
import com.example.taskscheduler.domain.UserAuth
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.domain.repos.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.coroutines.suspendCoroutine

class UserRepositoryImpl(
    private val userDataSource: UserDataSource,
    private val userAuth: UserAuth,
    private val userToUserEntityMapper: Mapper<User, UserEntity>,
    private val userEntityToUserMapper: Mapper<UserEntity, User>,
) : UserRepository {

    private val auth = Firebase.auth
    private val databaseUsersReference = Firebase.database.getReference(USERS)

    override suspend fun addUser(user: User) {
        userDataSource.addUser(userToUserEntityMapper.map(user))
    }

    override fun getUser(userId: String): Flow<User> {
        return userDataSource.getUser(userId).map {
            userEntityToUserMapper.map(it)
        }
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
        auth.currentUser?.let {
            databaseUsersReference.child(it.uid).child("onlineStatus").setValue(true)
        }
    }
}