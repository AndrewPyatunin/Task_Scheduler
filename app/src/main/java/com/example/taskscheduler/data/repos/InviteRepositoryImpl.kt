package com.example.taskscheduler.data.repos

import com.example.taskscheduler.data.datasources.InviteDataSource
import com.example.taskscheduler.data.datasources.UserDataSource
import com.example.taskscheduler.data.entities.InviteEntity
import com.example.taskscheduler.data.entities.UserEntity
import com.example.taskscheduler.data.entities.UserForInvitesEntity
import com.example.taskscheduler.data.mappers.Mapper
import com.example.taskscheduler.data.mappers.UserToUserEntityMapper
import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.Invite
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.domain.repos.InviteRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.coroutines.suspendCoroutine

class InviteRepositoryImpl(
    private val inviteDataSource: InviteDataSource,
    private val userDataSource: UserDataSource,
    private val inviteEntityToInviteMapper: Mapper<InviteEntity, Invite>,
    private val inviteToInviteEntityMapper: Mapper<Invite, InviteEntity>,
    private val userToUserForInvitesMapper: Mapper<User, UserForInvitesEntity>,
    private val userForInvitesToUserMapper: Mapper<UserForInvitesEntity, User>,
    private val userToUserEntityMapper: Mapper<User, UserEntity>,
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase,
    private val databaseInvitesReference: DatabaseReference,
    private val databaseUsersReference: DatabaseReference,
    private val databaseBoardsReference: DatabaseReference
) : InviteRepository {

    override suspend fun getInvites(scope: CoroutineScope) {

        databaseInvitesReference.child(auth.currentUser?.uid.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        scope.launch {
                            it.children.forEach {
                                it.getValue(Invite::class.java)?.let { invite ->
                                    addInvite(invite)
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    throw RuntimeException(error.message)
                }
            })
    }

    override suspend fun acceptInvite(user: User, invite: Invite) {
        val boardsList: ArrayList<String> = user.boards as ArrayList<String>
        val inviteBoardId = invite.boardId
        boardsList.add(inviteBoardId)
        databaseUsersReference.child(user.id).child("boards").setValue(boardsList)
        databaseBoardsReference.child(inviteBoardId).child("members")
            .updateChildren(mapOf(Pair(user.id, true)))
        clearInviteInDatabase(user, invite)
    }

    override suspend fun declineInvite(user: User, invite: Invite) {
        clearInviteInDatabase(user, invite)
    }

    override suspend fun clearInviteInDatabase(user: User, invite: Invite) {
        databaseInvitesReference.child(user.id).child(invite.boardId).removeValue()
        databaseUsersReference.child(user.id).child("invites").child(invite.boardId).removeValue()
        inviteDataSource.removeInvite(inviteToInviteEntityMapper.map(invite))
        userDataSource.addUser(userToUserEntityMapper.map(user))
    }

    override suspend fun inviteUser(
        userForInvite: User,
        currentUser: User,
        board: Board
    ): String = suspendCoroutine { continuation ->
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

    fun getInvitesFromRoom(): Flow<List<Invite>> {
        return inviteDataSource.getInvitesFlow().map { list ->
            list.map {
                inviteEntityToInviteMapper.map(it)
            }
        }
    }

    fun getUsersForInvites(): Flow<List<User>> {
        return inviteDataSource.getUsersForInvites().map { list ->
            list.map {
                userForInvitesToUserMapper.map(it)
            }
        }
    }
}