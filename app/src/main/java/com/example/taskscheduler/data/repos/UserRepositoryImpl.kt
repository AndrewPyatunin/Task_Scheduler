package com.example.taskscheduler.data.repos

import android.net.Uri
import android.util.Log
import com.example.taskscheduler.data.datasources.UserDataSource
import com.example.taskscheduler.data.entities.UserEntity
import com.example.taskscheduler.data.mappers.Mapper
import com.example.taskscheduler.domain.UserAuth
import com.example.taskscheduler.domain.repos.UserRepository
import com.example.taskscheduler.domain.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.coroutines.suspendCoroutine

class UserRepositoryImpl(
    private val userDataSource: UserDataSource,
    private val userAuth: UserAuth,
    private val userToUserEntityMapper: Mapper<User, UserEntity>,
    private val userEntityToUserMapper: Mapper<UserEntity, User>,
    private val databaseUsersReference: DatabaseReference,
    private val storageReference: StorageReference,
    private val auth: FirebaseAuth
) : UserRepository {

    override suspend fun addUser(user: User) {
        userDataSource.addUser(userToUserEntityMapper.map(user))
    }

    override fun getUser(userId: String): Flow<User> {
        return userDataSource.getUser(userId).map {
            userEntityToUserMapper.map(it)
        }
    }

    override suspend fun updateUserProfile(description: String, email: String) {
        val ref = auth.currentUser?.let {
            databaseUsersReference.child(it.uid)
        }
        if (description != "") {
            ref?.child("description")?.setValue(description)
//            it.resumeWith(Result.success(description))
        }
        if (email != "" && ref != null) {
            updateUserEmail(email, ref)
        }
    }

    override suspend fun update(uri: Uri?, name: String) {
        uri?.let {
            val resultUrl = upLoadUserAvatar(uri, name)
            databaseUsersReference.child(auth.currentUser!!.uid).child("uri").setValue(resultUrl)
        }
    }

    private suspend fun upLoadUserAvatar(uri: Uri, name: String) = suspendCoroutine { continuation ->
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
                userAuth.updateUserAvatar(it.result, name)
                val urlToFile = it.result.toString()
                continuation.resumeWith(Result.success(urlToFile))
            } else {
                continuation.resumeWith(Result.failure(RuntimeException("Ошибка загрузки!")))
            }
        }
    }

    override suspend fun updateUserEmail(email: String, ref: DatabaseReference): String = suspendCoroutine {
        auth.currentUser!!.updateEmail(email)
            .addOnCompleteListener { task ->
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