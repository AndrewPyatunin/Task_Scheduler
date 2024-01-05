package com.example.taskscheduler.data.repos

import com.example.taskscheduler.data.FirebaseConstants.BOARDS
import com.example.taskscheduler.data.FirebaseConstants.INVITES
import com.example.taskscheduler.data.FirebaseConstants.PATH_BOARDS
import com.example.taskscheduler.data.FirebaseConstants.PATH_INVITES
import com.example.taskscheduler.data.FirebaseConstants.PATH_MEMBERS
import com.example.taskscheduler.data.FirebaseConstants.USERS
import com.example.taskscheduler.data.database.TaskDatabaseDao
import com.example.taskscheduler.data.datasources.InviteDataSourceImpl
import com.example.taskscheduler.data.datasources.UserDataSourceImpl
import com.example.taskscheduler.data.mappers.InviteEntityToInviteMapper
import com.example.taskscheduler.data.mappers.InviteToInviteEntityMapper
import com.example.taskscheduler.data.mappers.UserToUserEntityMapper
import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.Invite
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.domain.repos.InviteRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.coroutines.suspendCoroutine

class InviteRepositoryImpl(
    dao: TaskDatabaseDao
) : InviteRepository {

    private val inviteDataSource = InviteDataSourceImpl(dao)
    private val userDataSource = UserDataSourceImpl(dao)
    private val inviteEntityToInviteMapper = InviteEntityToInviteMapper()
    private val inviteToInviteEntityMapper = InviteToInviteEntityMapper()
    private val userToUserEntityMapper = UserToUserEntityMapper()
    private val auth = Firebase.auth
    private val databaseInvitesReference = Firebase.database.getReference(INVITES)
    private val databaseUsersReference = Firebase.database.getReference(USERS)
    private val databaseBoardsReference = Firebase.database.getReference(BOARDS)

    override suspend fun getInvites(scope: CoroutineScope) = suspendCancellableCoroutine {

        databaseInvitesReference.child(auth.currentUser?.uid.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val invites = ArrayList<Invite>()
                    snapshot.children.forEach {
                        it.children.forEach {
                            it.getValue(Invite::class.java)?.let { invite ->
                                invites.add(invite) //add Invite to room
                            }
                        }
                    }
                    scope.launch(Dispatchers.IO) {
                        it.resumeWith(Result.success(addInvites(invites)))
                    }
                }

                override fun onCancelled(error: DatabaseError) = Unit
            })
    }

    override suspend fun acceptInvite(user: User, invite: Invite) {
        val inviteBoardId = invite.boardId
        databaseUsersReference.child(user.id).child(PATH_BOARDS)
            .updateChildren(mapOf(Pair(inviteBoardId, true)))
        databaseBoardsReference.child(inviteBoardId).child(PATH_MEMBERS)
            .updateChildren(mapOf(Pair(user.id, true)))
        clearInviteInDatabase(user.copy(boards = (user.boards as MutableMap).apply {
            this[inviteBoardId] = true
        }, invites = user.invites.filter {
            it.key != invite.id
        }), invite)
        // possible to update board
    }

    override suspend fun declineInvite(user: User, invite: Invite) {
        clearInviteInDatabase(user, invite)
    }

    override suspend fun clearInviteInDatabase(user: User, invite: Invite) {
        databaseInvitesReference.child(user.id).child(invite.boardId).removeValue()
        databaseUsersReference.child(user.id).child(PATH_INVITES).child(invite.boardId).removeValue()
        inviteDataSource.removeInvite(inviteToInviteEntityMapper.map(invite))
        userDataSource.addUser(userToUserEntityMapper.map(user))
    }

    override suspend fun inviteUser(
        userForInvite: User,
        currentUser: User,
        board: Board,
        scope: CoroutineScope
    ) = suspendCancellableCoroutine { continuation ->
        databaseInvitesReference.child(userForInvite.id)
            .child(board.id)//можно избавиться от получения данных из базы и брать их из room
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
                        val ref = databaseUsersReference.child(userForInvite.id).child(PATH_INVITES)
                        ref.updateChildren(mapOf(Pair(board.id, true)))
                        scope.launch(Dispatchers.IO) {
                            if (continuation.isActive) {
                                continuation.resumeWith(
                                    Result.success(
                                        userDataSource.addUser(
                                            userToUserEntityMapper.map(userForInvite.copy(invites = (userForInvite.invites as MutableMap).apply {
                                                put(board.id, true)
                                            }))
                                        )
                                    )
                                )
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) = Unit
            })
    }

    override suspend fun addInvite(invite: Invite) {
        inviteDataSource.addInvite(inviteToInviteEntityMapper.map(invite))
    }

    override suspend fun addInvites(inviteList: List<Invite>) {
        inviteDataSource.addInvites(inviteList.map { invite ->
            inviteToInviteEntityMapper.map(invite)
        })
    }


    override suspend fun clearAllInvitesInRoom() {
        inviteDataSource.clearAllInvites()
    }

    override fun getInvitesFromRoom(): Flow<List<Invite>> {
        return inviteDataSource.getInvitesFlow().map { list ->
            list.map {
                inviteEntityToInviteMapper.map(it)
            }
        }
    }


}