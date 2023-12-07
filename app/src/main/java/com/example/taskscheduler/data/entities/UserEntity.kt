package com.example.taskscheduler.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
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