package com.example.taskscheduler.data.modelsDb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "invites")
data class InviteDb(
    @PrimaryKey
    val id: String,
    val boardId: String,
    val userSenderId: String,
    val userName: String,
    val userLastName: String,
    val boardName: String
) {
}