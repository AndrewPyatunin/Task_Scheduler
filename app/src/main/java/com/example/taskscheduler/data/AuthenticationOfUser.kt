package com.example.taskscheduler.data

import android.net.Uri
import android.util.Log
import com.example.taskscheduler.data.mappers.MapperForUserAndUserDb
import com.example.taskscheduler.domain.AuthUser
import com.example.taskscheduler.domain.models.User
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

class AuthenticationOfUser @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val mapperForUserAndUserDb: MapperForUserAndUserDb,
    private val databaseUsersReference: DatabaseReference,
    private val storageReference: StorageReference,
    private val dao: TaskDatabaseDao,
    private val auth: FirebaseAuth
) : AuthUser {
    interface UrlCallback {
        fun onUrlCallback(url: String)
    }

    override fun signUp(
        email: String,
        password: String,
        name: String,
        lastName: String,
        uri: Uri?,
        scope: CoroutineScope
    ): Flow<User> = callbackFlow {
//        scope.launch {
//            flowUserAuth.emit(UserAuthState.Loading)
//        }
        val successListener = object : OnSuccessListener<AuthResult> {
            override fun onSuccess(p0: AuthResult?) {
                val userId = p0?.user?.uid ?: return
                if (uri != null) {
                    uploadUserAvatar(uri, "$name $lastName", scope, object : UrlCallback {
                        override fun onUrlCallback(url: String) {
                            val user = User(userId, name, lastName, email, true, emptyList(), url)
                            databaseUsersReference.child(userId).setValue(user)
                            trySend(user)
//                        _user.value = user
                            scope.launch {
                                localDataSource.addUser(mapperForUserAndUserDb.mapToDb(user))
//                            _userFlow.emit(user)
//                            flowUserAuth.emit()
                            }
                        }
                    })
                }

            }
        }
        val failureListener = object : OnFailureListener {
            override fun onFailure(p0: Exception) {
                throw RuntimeException(p0.message?: "Неизвестная ошибка!")
            }

        }
        val creationTask = auth.createUserWithEmailAndPassword(email, password)
        creationTask.addOnSuccessListener(successListener)
            .addOnFailureListener(failureListener)
        awaitClose {
        }
    }


    private fun updateUserAvatar(uri: Uri, name: String, scope: CoroutineScope) {
        val user = auth.currentUser
        val profileUpdates = userProfileChangeRequest {
            photoUri = uri
            displayName = name
        }


        user?.updateProfile(profileUpdates)
            ?.addOnCompleteListener {
                if (it.isSuccessful) {

//                    updateData(scope = scope)
//                    Toast.makeText(
//                        getApplication(),
//                        "Обновление данных пользователя прошло успешно",
//                        Toast.LENGTH_SHORT
//                    ).show()
                    Log.i("USER_FIREBASE_SUCCESS", auth.currentUser.toString())
//                    scope.launch {
//                        flowToast.emit("Обновление данных пользователя прошло успешно")
//                    }

//                    _success.value = auth.currentUser
                }
            }?.addOnFailureListener {
                throw RuntimeException(it.message)
            }
    }


    private fun uploadUserAvatar(
        uri: Uri,
        name: String,
        scope: CoroutineScope,
        callback: UrlCallback
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
                updateUserAvatar(it.result, name, scope)
                val urlToFile = it.result.toString()
                callback.onUrlCallback(urlToFile)
            } else {
                throw RuntimeException(it.result.toString())
//                scope.launch {
//                    flowToast.emit(it.result.toString())
//                }
//                Toast.makeText(getApplication(), "${it.result}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun login(email: String, password: String, auth: FirebaseAuth, scope: CoroutineScope) {
        val user = dao.getUser(email)
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {

        }.addOnFailureListener {

//            _error.value = it.message
        }
//        if (password == user.password) {
//            MyDatabaseConnection.userId = user.id
//            _success.value = user ?: User()
//        }
    }
}