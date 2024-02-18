package com.example.taskscheduler.data.repos

import android.net.Uri
import com.example.taskscheduler.MyDatabaseConnection
import com.example.taskscheduler.data.FirebaseConstants.IMAGES
import com.example.taskscheduler.data.FirebaseConstants.PATH_ONLINE_STATUS
import com.example.taskscheduler.data.FirebaseConstants.USERS
import com.example.taskscheduler.data.datasources.UserDataSource
import com.example.taskscheduler.data.entities.UserEntity
import com.example.taskscheduler.data.mappers.Mapper
import com.example.taskscheduler.domain.UserAuth
import com.example.taskscheduler.domain.models.User
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserAuthentication @Inject constructor(
    private val userDataSource: UserDataSource,
    private val userToUserEntityMapper: Mapper<User, UserEntity>
) : UserAuth {

    private val auth = Firebase.auth
    private val databaseUsersReference = Firebase.database.getReference(USERS)
    private val storageReference = Firebase.storage.getReference(IMAGES)

    override suspend fun signUp(
        email: String,
        password: String,
        name: String,
        lastName: String,
        uri: Uri?,
        scope: CoroutineScope
    ): User = suspendCoroutine { continuation ->

        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener { authRes ->
            val userId = authRes.user?.uid ?: return@addOnSuccessListener
            uri?.let {
                scope.launch(Dispatchers.IO) {
                    uploadUserAvatar(uri, "$name $lastName").onSuccess { uri ->
                        val user =
                            User(userId, name, lastName, email, true, emptyMap(), uri)
                        databaseUsersReference.child(userId).setValue(user)
                        userDataSource.addUser(userToUserEntityMapper.map(user))
                        continuation.resumeWith(Result.success(user))
                    }.onFailure {
                        continuation.resumeWith(Result.failure(it))
                    }
                }
            }
        }.addOnFailureListener {
            OnFailureListener { exception ->
                continuation.resumeWith(Result.failure(exception))
            }
        }
    }

    override fun updateUserAvatar(uri: Uri, name: String) {

        val user = auth.currentUser
        val profileUpdates = userProfileChangeRequest {
            photoUri = uri
            displayName = name
        }

        user?.updateProfile(profileUpdates)
            ?.addOnFailureListener {
                throw RuntimeException(it.message)
            }
    }

    override suspend fun uploadUserAvatar(
        uri: Uri,
        name: String
    ) = suspendCoroutine { continuation ->
        val imageRef = storageReference.child("images/${uri.lastPathSegment}")
        imageRef.putFile(uri).continueWithTask {
            if (!it.isSuccessful) {
                it.exception?.let { exception ->
                    throw exception
                }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener {
            if (it.isSuccessful) {
                updateUserAvatar(it.result, name)
                val urlToFile = it.result.toString()
                continuation.resume(Result.success(urlToFile))
            } else {
                continuation.resume(Result.failure<String>(RuntimeException(it.result.toString())))
            }
        }
    }

    override suspend fun getUser(userId: String, scope: CoroutineScope) =
        suspendCoroutine { continuation ->
            databaseUsersReference.child(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(User::class.java)
                        scope.launch(Dispatchers.IO) {
                            user?.let {
                                continuation.resumeWith(
                                    Result.success(
                                        userDataSource.addUser(
                                            userToUserEntityMapper.map(user)
                                        )
                                    )
                                )
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        continuation.resumeWith(Result.failure(error.toException()))
                    }
                })
        }

    override suspend fun login(email: String, password: String, auth: FirebaseAuth, scope: CoroutineScope) =
        suspendCoroutine { continuation ->
            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                auth.currentUser?.let {
                    databaseUsersReference.child(it.uid)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val user = snapshot.getValue(User::class.java)
                                scope.launch(Dispatchers.IO) {
                                    user?.let {
                                        continuation.resumeWith(
                                            Result.success(
                                                userDataSource.addUser(
                                                    userToUserEntityMapper.map(user)
                                                )
                                            )
                                        )
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                continuation.resumeWith(Result.failure(error.toException()))
                            }
                        })
                }
            }.addOnFailureListener {
                continuation.resumeWith(Result.failure(it))
            }
        }


    override fun logout(user: User, scope: CoroutineScope) {
        (auth.currentUser?.uid ?: MyDatabaseConnection.userId)?.let {
            databaseUsersReference.child(it).child(PATH_ONLINE_STATUS).setValue(false)
        }
        scope.launch(Dispatchers.IO) {
            userDataSource.addUser(userToUserEntityMapper.map(user.copy(onlineStatus = false)))
        }
    }
}