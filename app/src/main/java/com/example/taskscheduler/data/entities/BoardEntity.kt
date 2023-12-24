package com.example.taskscheduler.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "boards")
data class BoardEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val creatorId: String,
    val backgroundUrl: String,
    val members: Map<String, Boolean>,
    val listOfNotesIds: Map<String, Boolean>
)