package com.example.taskscheduler.data.modelsDb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usersForInvites")
data class UserForInvitesDb(
    @PrimaryKey
    val id: String,
    val name: String,
    val lastName: String,
    val email: String,
    val onlineStatus: Boolean,
    val boards: List<String>,
    val uri: String,
    val description: String,
    val invites: Map<String, Boolean>
) {
}