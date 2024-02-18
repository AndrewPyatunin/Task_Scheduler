package com.example.taskscheduler.data.repos

import com.example.taskscheduler.data.FirebaseConstants.BOARDS
import com.example.taskscheduler.data.FirebaseConstants.INVITES
import com.example.taskscheduler.data.FirebaseConstants.PATH_BOARDS
import com.example.taskscheduler.data.FirebaseConstants.PATH_INVITES
import com.example.taskscheduler.data.FirebaseConstants.PATH_MEMBERS
import com.example.taskscheduler.data.FirebaseConstants.USERS
import com.example.taskscheduler.data.datasources.InviteDataSource
import com.example.taskscheduler.data.datasources.UserDataSource
import com.example.taskscheduler.data.entities.InviteEntity
import com.example.taskscheduler.data.entities.UserEntity
import com.example.taskscheduler.data.mappers.Mapper
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject

class InviteRepositoryImpl @Inject constructor(
    private val inviteDataSource: InviteDataSource,
    private val userDataSource: UserDataSource,
    private val inviteEntityToInviteMapper: Mapper<InviteEntity, Invite>,
    private val inviteToInviteEntityMapper: Mapper<Invite, InviteEntity>,
    private val userToUserEntityMapper: Mapper<User, UserEntity>,
) : InviteRepository {

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
        clearInviteInDatabase(user.copy(boards = user.boards.plus(inviteBoardId to true),
            invites = user.invites.filter {
                it.key != invite.id
            }), invite
        )
        // possible to update board
    }

    override suspend fun clearInviteInDatabase(user: User, invite: Invite) {
        databaseInvitesReference.child(user.id).child(invite.boardId).removeValue()
        databaseUsersReference.child(user.id).child(PATH_INVITES).child(invite.boardId)
            .removeValue()
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
                                            userToUserEntityMapper.map(
                                                userForInvite.copy(
                                                    invites = (userForInvite.invites.plus(
                                                        board.id to true
                                                    ))
                                                )
                                            )
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
        inviteDataSource.addInvites(inviteList.map(inviteToInviteEntityMapper::map))
    }


    override suspend fun clearAllInvitesInRoom() {
        inviteDataSource.clearAllInvites()
    }

    override fun getInvitesFromRoom(): Flow<List<Invite>> {
        return inviteDataSource.getInvitesFlow().map {
            it.map(inviteEntityToInviteMapper::map)
        }
    }
}