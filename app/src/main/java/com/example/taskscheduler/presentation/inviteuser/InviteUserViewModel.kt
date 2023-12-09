package com.example.taskscheduler.presentation.inviteuser

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.Invite
import com.example.taskscheduler.domain.models.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class InviteUserViewModel(board: Board) : ViewModel() {
    var userInList = false
    lateinit var database: FirebaseDatabase
    private val auth = Firebase.auth
    private val firebaseDatabase = Firebase.database
    val databaseUsersReference = firebaseDatabase.getReference("Users")
    val databaseInvitesReference = firebaseDatabase.getReference("Invites")
    private val _user = MutableLiveData<FirebaseUser>()
    val user: LiveData<FirebaseUser>
        get() = _user

    private val _listUsers = MutableLiveData<List<User>>()
    val listUsers: LiveData<List<User>>
        get() = _listUsers

    private val _success = MutableLiveData<String>()
    val success: LiveData<String>
        get() = _success

    init {
        auth.addAuthStateListener {
            _user.value = auth.currentUser
        }


        databaseUsersReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val usersFromDb = ArrayList<User>()
                for (dataSnapshot in snapshot.children) {
                    val user = dataSnapshot.getValue(User::class.java)
                    if (user != null && user.id != auth.currentUser?.uid &&
                        user.id !in board.members && board.id !in user.invites.keys) {
                        usersFromDb.add(user)
                    }
                }
                _listUsers.value = usersFromDb
            }

            override fun onCancelled(error: DatabaseError) {
                auth.signOut()
            }

        })
    }

    fun inviteUser(userForInvite: User, currentUser: User, board: Board) {
        databaseInvitesReference.child(userForInvite.id).child(board.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChildren()) {

                    } else {
                        val pushInvite =
                            databaseInvitesReference.child(userForInvite.id).child(board.id).push()
                        val inviteId = pushInvite.key.toString()
                        pushInvite.setValue(
                            Invite(
                                inviteId,
                                board.id,
                                currentUser.id,
                                currentUser.name,
                                currentUser.lastName,
                                board.title
                            )
                        )
                        val ref = databaseUsersReference.child(userForInvite.id).child("invites")
                        val map = HashMap<String, Any>()
                        map[board.id] = true
                        ref.updateChildren(map)
                        _success.value = "Приглашение успешно отправлено"

                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

    }
}