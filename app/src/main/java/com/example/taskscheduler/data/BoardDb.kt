package com.example.taskscheduler.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "boards")
data class BoardDb(
    @PrimaryKey
    val id: String,
    val title: String,
    val creatorId: String,
    val backgroundUrl: String,
    val members: List<String>,
    val listOfNotesIds: List<String>
) {
}