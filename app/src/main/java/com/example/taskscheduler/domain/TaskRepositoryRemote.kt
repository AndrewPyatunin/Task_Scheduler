package com.example.taskscheduler.domain

import android.net.Uri
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference

interface TaskRepositoryRemote: TaskRepository {

    fun signUp(email: String, password: String, name: String, lastName: String, uri: Uri?): LiveData<User>

    fun updateUserAvatar(uri: Uri, name: String): LiveData<FirebaseUser>

    fun uploadUserAvatar(uri: Uri, name: String, callback: UrlCallback)

    fun updateUserProfile(description: String, email: String): LiveData<String>

    fun update(uri: Uri?, name: String)

    fun updateUserEmail(email: String, ref: DatabaseReference): LiveData<String>

    fun updateStatus()

    fun getInvites(): LiveData<List<Invite>>

    fun acceptInvite(user: User, invite: Invite)

    fun declineInvite(user: User, invite: Invite)

    fun clearInviteInDatabase(userId: String, inviteBoardId: String)

    fun inviteUser(userForInvite: User, currentUser: User, board: Board): LiveData<String>

    fun logout()


    interface UrlCallback {
        fun onUrlCallback(url: String)
    }
}