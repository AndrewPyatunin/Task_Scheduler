package com.example.taskscheduler.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var id: String = "",
    val name: String = "",
    val lastName: String = "",
    val email: String = "",
    var onlineStatus: Boolean = false,
    var boards: List<String> = emptyList(),
    var uri: String = "",
    var description: String = "",
    var invites: Map<String, Boolean> = emptyMap()

) :
    Parcelable