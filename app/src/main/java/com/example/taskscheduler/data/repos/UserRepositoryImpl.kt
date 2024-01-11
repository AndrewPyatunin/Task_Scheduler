package com.example.taskscheduler.data.repos

import android.net.Uri
import android.util.Log
import com.example.taskscheduler.data.FirebaseConstants.IMAGES
import com.example.taskscheduler.data.FirebaseConstants.PATH_DESCRIPTION
import com.example.taskscheduler.data.FirebaseConstants.PATH_EMAIL
import com.example.taskscheduler.data.FirebaseConstants.PATH_ONLINE_STATUS
import com.example.taskscheduler.data.FirebaseConstants.PATH_URI
import com.example.taskscheduler.data.FirebaseConstants.USERS
import com.example.taskscheduler.data.database.UserDao
import com.example.taskscheduler.data.datasources.UserDataSourceImpl
import com.example.taskscheduler.data.mappers.UserEntityToUserMapper
import com.example.taskscheduler.data.mappers.UserToUserEntityMapper
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.domain.repos.UserRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserRepositoryImpl(
    dao: UserDao
) : UserRepository {

    private val userDataSource = UserDataSourceImpl(dao)
    private val userToUserEntityMapper = UserToUserEntityMapper()
    private val userEntityToUserMapper = UserEntityToUserMapper()
    private val auth = Firebase.auth
    private val databaseUsersReference = Firebase.database.getReference(USERS)
    private val storageReference = Firebase.storage.getReference(IMAGES)

    override suspend fun addUser(user: User) {
        userDataSource.addUser(userToUserEntityMapper.map(user))
    }

    override suspend fun getUser(userId: String): User {
        return userEntityToUserMapper.map(userDataSource.getUser(userId))
    }

    override suspend fun updateUserProfile(
        description: String,
        email: String,
        user: User,
        scope: CoroutineScope
    ) = suspendCancellableCoroutine {
        val ref = auth.currentUser?.let {
            databaseUsersReference.child(it.uid)
        }
        if (description != "") {
            ref?.child(PATH_DESCRIPTION)?.setValue(description)
            scope.launch(Dispatchers.IO) {
                if (it.isActive) {
                    it.resumeWith(Result.success(addUser(user.copy(description = description))))
                }
            }
        }
        if (email != "" && ref != null) {
            scope.launch(Dispatchers.IO) {
                updateUserEmail(email, ref)
                if (it.isActive) it.resumeWith(Result.success(addUser(user.copy(email = email))))
            }
        }
    }

    override suspend fun update(uri: Uri?, name: String, user: User, scope: CoroutineScope) =
        suspendCancellableCoroutine { continuation ->
            uri?.let {
                scope.launch(Dispatchers.IO) {
                    uploadUserAvatar(uri, name).onSuccess {
                        auth.currentUser?.uid?.let { userId ->
                            databaseUsersReference.child(userId).child(PATH_URI).setValue(it)
                            if (continuation.isActive) {
                                continuation.resumeWith(Result.success(addUser(user.copy(uri = it))))
                            }
                        }
                    }
                }

            }
        }

    override suspend fun uploadUserAvatar(
        uri: Uri,
        name: String
    ) = suspendCancellableCoroutine { continuation ->
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
                if (continuation.isActive) continuation.resume(Result.success(urlToFile))
            } else {
                if (continuation.isActive)
                    continuation.resume(Result.failure<String>(RuntimeException(it.result.toString())))
            }
        }
    }

    override suspend fun updateUserEmail(email: String, ref: DatabaseReference): String =
        suspendCoroutine {
            auth.currentUser?.updateEmail(email)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        ref.child(PATH_EMAIL).setValue(email)
                        it.resumeWith(Result.success(email))
                    }
                }
        }

    override fun updateStatus() {
        auth.addAuthStateListener { auth ->
            auth.currentUser?.let {
                databaseUsersReference.child(it.uid).child(PATH_ONLINE_STATUS).setValue(false)
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

    override suspend fun addAllUsers(scope: CoroutineScope) =
        suspendCancellableCoroutine { continuation ->
            databaseUsersReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val users = ArrayList<User>()
                    snapshot.children.forEach {
                        it.getValue(User::class.java)?.let { it1 -> users.add(it1) }
                    }
                    scope.launch(Dispatchers.IO) {
                        if (continuation.isActive) {
                            continuation.resumeWith(Result.success(userDataSource.addAllUsers(users.map {
                                userToUserEntityMapper.map(it)
                            })))
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) = Unit

            })
        }

    private fun updateUserAvatar(uri: Uri, name: String) {

        val user = auth.currentUser
        val profileUpdates = userProfileChangeRequest {
            photoUri = uri
            displayName = name
        }

        user?.updateProfile(profileUpdates)
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.i("USER_FIREBASE_SUCCESS", auth.currentUser.toString())
                }
            }?.addOnFailureListener {
                throw RuntimeException(it.message)
            }
    }

    override suspend fun clearAllUsers() {
        userDataSource.clearAllUsersInRoom()
    }
}