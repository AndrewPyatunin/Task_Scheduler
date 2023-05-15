package com.example.taskscheduler.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Invite(
    var id: String = "",
    var boardId: String = "",
    var userSenderId: String = "",
    var userName: String = "",
    var userLastName: String = "",
    var boardName: String = ""
): Parcelable
