package com.example.taskscheduler.domain.models

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity("invites")
data class Invite(
    var id: String = "",
    var boardId: String = "",
    var userSenderId: String = "",
    var userName: String = "",
    var userLastName: String = "",
    var boardName: String = ""
): Parcelable
