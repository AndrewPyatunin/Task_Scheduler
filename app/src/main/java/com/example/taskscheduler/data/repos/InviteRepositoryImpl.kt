package com.example.taskscheduler.data.repos

import android.util.Log
import com.example.taskscheduler.data.datasources.InviteDataSource
import com.example.taskscheduler.data.datasources.UserDataSource
import com.example.taskscheduler.data.entities.InviteEntity
import com.example.taskscheduler.data.entities.UserForInvitesEntity
import com.example.taskscheduler.data.mappers.Mapper
import com.example.taskscheduler.domain.repos.InviteRepository
import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.Invite
import com.example.taskscheduler.domain.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.suspendCoroutine

class InviteRepositoryImpl(
    private val inviteDataSource: InviteDataSource,
    private val userDataSource: UserDataSource,
    private val inviteEntityToInviteMapper: Mapper<InviteEntity, Invite>,
    private val inviteToInviteEntityMapper: Mapper<Invite, InviteEntity>,
    private val userToUserForInvitesMapper: Mapper<User, UserForInvitesEntity>,
    private val userForInvitesToUserMapper: Mapper<UserForInvitesEntity, User>,
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase,
    private val databaseInvitesReference: DatabaseReference,
    private val databaseUsersReference: DatabaseReference,
    private val databaseBoardsReference: DatabaseReference
) : InviteRepository {

    override fun getInvites(): Flow<List<Invite>> = callbackFlow {
        val invitesListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listInvites = ArrayList<Invite>()
                snapshot.children.forEach {
                    it.children.forEach {
                        it.getValue(Invite::class.java)?.let { invite ->
                            listInvites.add(invite)
                        }
                    }
                }
                Log.i("USER_INVITE_LIST_SIZE", listInvites.size.toString())
                trySend(listInvites)
            }

            override fun onCancelled(error: DatabaseError) {
                throw RuntimeException(error.message)
            }
        }
        val ref = databaseInvitesReference.child(auth.currentUser?.uid.toString())
        ref.addValueEventListener(invitesListener)
        awaitClose {
            ref.removeEventListener(invitesListener)
        }
    }

    override fun acceptInvite(user: User, invite: Invite) {
        val boardsList: ArrayList<String> = user.boards as ArrayList<String>
        val inviteBoardId = invite.boardId
        boardsList.add(inviteBoardId)
        databaseUsersReference.child(user.id).child("boards").setValue(boardsList)
        val listMembers = ArrayList<String>()
        databaseBoardsReference.child(inviteBoardId).child("members")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    databaseBoardsReference.removeEventListener(this)
                    snapshot.children.forEach { dataSnapshot ->
                        listMembers.add(dataSnapshot.value.toString())
                        Log.i("USER_MEMBERS_FROM", dataSnapshot.value.toString())
                    }
                    listMembers.add(user.id)
                    databaseBoardsReference.child(inviteBoardId).child("members")
                        .setValue(listMembers)
                }

                override fun onCancelled(error: DatabaseError) {
                    throw RuntimeException(error.message)
                }
            })
        clearInviteInDatabase(user.id, inviteBoardId)
    }

    override fun declineInvite(user: User, invite: Invite) {
        clearInviteInDatabase(user.id, invite.boardId)
    }

    override fun clearInviteInDatabase(userId: String, inviteBoardId: String) {
        databaseInvitesReference.child(userId).child(inviteBoardId).removeValue()
        databaseUsersReference.child(userId).child("invites").child(inviteBoardId).removeValue()
//        inviteDataSource.removeInvite()
//        userDataSource.addUser()
    }

    override suspend fun inviteUser(
        userForInvite: User,
        currentUser: User,
        board: Board
    ): String = suspendCoroutine { continuation ->
        databaseInvitesReference.child(userForInvite.id).child(board.id)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.hasChildren()) {
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
                        continuation.resumeWith(Result.success("Приглашение успешно отправлено"))
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWith(Result.failure(error.toException()))
                }
            })
    }

    override suspend fun addInvite(invite: Invite) {
        inviteDataSource.addInvite(inviteToInviteEntityMapper.map(invite))
    }

    override suspend fun addUserForInvites(user: User) {
        inviteDataSource.addUserForInvites(userToUserForInvitesMapper.map(user))
    }
}