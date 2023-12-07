package com.example.taskscheduler.domain.models

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.parcelize.Parcelize

@Parcelize
data class Invite(
    val id: String = "",
    val boardId: String = "",
    val userSenderId: String = "",
    val userName: String = "",
    val userLastName: String = "",
    val boardName: String = ""
): Parcelable
