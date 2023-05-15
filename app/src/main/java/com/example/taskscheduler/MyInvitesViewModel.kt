package com.example.taskscheduler

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.taskscheduler.domain.Invite
import com.example.taskscheduler.domain.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MyInvitesViewModel : ViewModel() {
    private val firebaseDatabase = Firebase.database
    val auth = Firebase.auth
    val databaseInvitesReference = firebaseDatabase.getReference("Invites")
    val databaseBoardsReference = firebaseDatabase.getReference("Boards")
    val databaseUsersReference = firebaseDatabase.getReference("Users")

    private val _invitesList = MutableLiveData<List<Invite>>()
    val inviteList: LiveData<List<Invite>>
        get() = _invitesList
    init {
        getInvites()
    }

    fun acceptInvite(user: User, invite: Invite) {
        val boardsList: ArrayList<String> = user.boards as ArrayList<String>
        val inviteBoardId = invite.boardId
        boardsList.add(inviteBoardId)
        databaseUsersReference.child(user.id).child("boards").setValue(boardsList)
        val listMembers = ArrayList<String>()
        databaseBoardsReference.child(inviteBoardId).child("members").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                databaseBoardsReference.removeEventListener(this)
                for (dataSnapshot in snapshot.children) {
                    listMembers.add(dataSnapshot.value.toString())
                    Log.i("USER_MEMBERS_FROM", dataSnapshot.value.toString())
                }
                listMembers.add(user.id)
                databaseBoardsReference.child(inviteBoardId).child("members").setValue(listMembers)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
        databaseInvitesReference.child(user.id).child(inviteBoardId).removeValue()
        databaseUsersReference.child(user.id).child("invites").child(inviteBoardId).removeValue()

    }

    private fun getInvites() {
        databaseInvitesReference.child(auth.currentUser?.uid.toString()).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listInvites = ArrayList<Invite>()
                for (dataSnapshot in snapshot.children) {
                    for (data in dataSnapshot.children) {
                        data.getValue(Invite::class.java)?.let { listInvites.add(it) }
                    }
                }
                Log.i("USER_INVITE_LIST_SIZE", listInvites.size.toString())
                _invitesList.value = listInvites
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}