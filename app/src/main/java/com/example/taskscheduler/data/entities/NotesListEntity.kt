package com.example.taskscheduler.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "listsOfNotes")
data class NotesListEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val creatorId: String,
    val listNotes: Map<String, Boolean>
)