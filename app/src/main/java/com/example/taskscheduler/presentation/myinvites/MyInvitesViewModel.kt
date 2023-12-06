package com.example.taskscheduler.presentation.myinvites

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.taskscheduler.domain.models.Invite
import com.example.taskscheduler.domain.models.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MyInvitesViewModel : ViewModel() {
    private val firebaseDatabase = Firebase.database
    val auth = Firebase.auth
    private val databaseInvitesReference = firebaseDatabase.getReference("Invites")
    val databaseBoardsReference = firebaseDatabase.getReference("Boards")
    private val databaseUsersReference = firebaseDatabase.getReference("Users")

    private val _invitesList = MutableLiveData<List<Invite>>()
    val inviteList: LiveData<List<Invite>>
        get() = _invitesList

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    init {
        getUserInfo()
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
                logout()
            }
        })
        clearInviteInDatabase(user.id, inviteBoardId)
    }

    fun declineInvite(user: User, invite: Invite) {
        clearInviteInDatabase(user.id, invite.boardId)
    }

    private fun clearInviteInDatabase(userId: String, inviteBoardId: String) {

        databaseInvitesReference.child(userId).child(inviteBoardId).removeValue()
        databaseUsersReference.child(userId).child("invites").child(inviteBoardId).removeValue()
    }


    private fun getUserInfo() {
        databaseUsersReference.child(auth.currentUser?.uid ?: "").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                _user.value = user ?: User()
            }

            override fun onCancelled(error: DatabaseError) {
                logout()
            }

        })
    }
    fun logout() {
        auth.signOut()
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
                logout()
            }

        })
    }
}