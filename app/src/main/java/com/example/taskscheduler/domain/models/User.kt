package com.example.taskscheduler.domain.models

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: String = "",
    val name: String = "",
    val lastName: String = "",
    val email: String = "",
    val onlineStatus: Boolean = false,
    val boards: List<String> = emptyList(),
    val uri: String = "",
    val description: String = "",
    val invites: Map<String, Boolean> = emptyMap()

) : Parcelable