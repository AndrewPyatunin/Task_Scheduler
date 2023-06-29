package com.example.taskscheduler

import android.net.Uri
import androidx.lifecycle.LiveData
import com.example.taskscheduler.domain.BackgroundImage
import com.example.taskscheduler.domain.NewCallback
import com.example.taskscheduler.domain.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object MyDatabaseConnection : DatabaseConnection {
    val database = Firebase.database
    private val databaseUsersRef = database.getReference("Users")
    val databaseBoardsRef = database.getReference("Boards")
    private val databaseImagesReference = database.getReference("ImageUrls")
    var userFrom = User()
    var uri: Uri? = null
    var currentPosition = 0
    var list = emptyList<BackgroundImage>()
    var updated = true
//    override fun <T>queryUser(ref: DatabaseReference, liveData: LiveData<T>) {
//        ref.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                snapshot.getValue()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//
//            }
//
//        })
//    }
    fun onCallbackReady() {
        getBackgroundImages(object : NewCallback {
            override fun callbackNew(list: List<BackgroundImage>) {
                this@MyDatabaseConnection.list = list
            }

        })
    }
    override fun getBackgroundImages(callback: NewCallback)  {
        databaseImagesReference.get().addOnSuccessListener {
            val items = it.children.mapIndexed { index, dataSnapshot ->
                BackgroundImage("$index", dataSnapshot.value.toString(), false)
            }
            callback.callbackNew(items)
        }
    }

    override fun query(user: FirebaseUser) {
        databaseUsersRef.child(user.uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userFrom = snapshot.getValue(User::class.java) ?: User()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun getBoard() {
        TODO("Not yet implemented")
    }

    override fun getBackgroundImages(): List<BackgroundImage> {
        TODO("Not yet implemented")
    }

}
