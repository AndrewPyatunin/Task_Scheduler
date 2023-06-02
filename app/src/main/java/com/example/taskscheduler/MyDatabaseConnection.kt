package com.example.taskscheduler

import androidx.lifecycle.LiveData
import com.example.taskscheduler.domain.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MyDatabaseConnection : DatabaseConnection {
    val database = Firebase.database
    val databaseUsersRef = database.getReference("Users")
    val databaseBoardsRef = database.getReference("Boards")
    companion object {
        var userFrom = User()
    }

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

}
