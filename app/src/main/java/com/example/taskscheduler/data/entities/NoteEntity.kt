package com.example.taskscheduler.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.taskscheduler.domain.CheckNoteItem
import com.example.taskscheduler.domain.UrgencyOfNote
import com.example.taskscheduler.domain.models.User

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val creatorId: String,
    val members: List<String>,
    val description: String,
    val date: String,
    val listOfTasks: List<CheckNoteEntity>,
    val priority: UrgencyOfNote
)
