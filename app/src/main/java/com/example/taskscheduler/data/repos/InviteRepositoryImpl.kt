package com.example.taskscheduler.data.repos

import com.example.taskscheduler.data.FirebaseConstants.BOARDS
import com.example.taskscheduler.data.FirebaseConstants.INVITES
import com.example.taskscheduler.data.FirebaseConstants.USERS
import com.example.taskscheduler.data.TaskDatabaseDao
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
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

    override suspend fun getInvites(scope: CoroutineScope) {

        databaseInvitesReference.child(auth.currentUser?.uid.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        GlobalScope.launch {
                            it.children.forEach {
                                it.getValue(Invite::class.java)?.let { invite ->
                                    addInvite(invite)
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) = Unit
            })
    }

    override suspend fun acceptInvite(user: User, invite: Invite) {
        val inviteBoardId = invite.boardId
        databaseUsersReference.child(user.id).child("boards").updateChildren(mapOf(Pair(inviteBoardId, true)))
        databaseBoardsReference.child(inviteBoardId).child("members")
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
            .addListenerForSingleValueEvent(object : ValueEventListener {
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
                        ref.updateChildren(mapOf(Pair(board.id, true)))
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