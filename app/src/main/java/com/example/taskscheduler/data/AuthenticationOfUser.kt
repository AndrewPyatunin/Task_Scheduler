package com.example.taskscheduler.data

import android.net.Uri
import android.util.Log
import com.example.taskscheduler.domain.AuthUser
import com.example.taskscheduler.domain.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
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

    private var _userFlow = MutableSharedFlow<User>()
    val userFlow = _userFlow.asSharedFlow()

    fun flowOf(): Flow<User> = userFlow

    private val flowToast = MutableSharedFlow<String>()

    override fun flowRegistrToast() = flowToast.asSharedFlow()

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

        val value = auth.createUserWithEmailAndPassword(email, password)
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            val userId = it.user?.uid ?: return@addOnSuccessListener
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

        }.addOnFailureListener {
//            scope.launch {
////                _toastFlow.emit(it.message!!)
//                flowToast.emit(it.message ?: "Неизвестная ошибка!")
//            }
            throw RuntimeException(it.message ?: "Неизвестная ошибка!")
//            _error.value = it.message
        }
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


    private var _toastFlow = MutableSharedFlow<String>()
    val toastFlow = _toastFlow.asSharedFlow()

    fun updateData(scope: CoroutineScope) = scope.launch(Dispatchers.Main) {
        _toastFlow.emit("Обновление данных произошло успешно")
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
            scope.launch {
                _toastFlow.emit(it.message!!)
            }

//            _error.value = it.message
        }
//        if (password == user.password) {
//            MyDatabaseConnection.userId = user.id
//            _success.value = user ?: User()
//        }
    }
}