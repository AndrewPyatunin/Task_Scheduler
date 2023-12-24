package com.example.taskscheduler.data.repos

import android.net.Uri
import android.util.Log
import com.example.taskscheduler.data.FirebaseConstants.IMAGES
import com.example.taskscheduler.data.FirebaseConstants.USERS
import com.example.taskscheduler.data.TaskDatabaseDao
import com.example.taskscheduler.data.datasources.UserDataSource
import com.example.taskscheduler.data.entities.UserEntity
import com.example.taskscheduler.data.mappers.Mapper
import com.example.taskscheduler.domain.UserAuth
import com.example.taskscheduler.domain.models.User
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserAuthentication(
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

        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            object : OnSuccessListener<AuthResult> {
                override fun onSuccess(authResult: AuthResult?) {
                    val userId = authResult?.user?.uid ?: return
                    uri?.let {
                        scope.launch {
                            val resultUrl = uploadUserAvatar(uri, "$name $lastName")
                            resultUrl.onSuccess {
                                val user =
                                    User(userId, name, lastName, email, true, emptyList(), it)
                                databaseUsersReference.child(userId).setValue(user)
                                continuation.resumeWith(Result.success(user))
                                userDataSource.addUser(userToUserEntityMapper.map(user))
                            }.onFailure {
                                continuation.resumeWith(Result.failure(it))
                            }
                        }
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
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.i("USER_FIREBASE_SUCCESS", auth.currentUser.toString())
                }
            }?.addOnFailureListener {
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
            Log.i("USER_URL", it.result.toString())
            if (it.isSuccessful) {
                updateUserAvatar(it.result, name)
                val urlToFile = it.result.toString()
                continuation.resume(Result.success(urlToFile))
            } else {
                throw RuntimeException(it.result.toString())
            }
        }
    }



    fun login(email: String, password: String, auth: FirebaseAuth) {
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {

        }.addOnFailureListener {

        }
    }
}