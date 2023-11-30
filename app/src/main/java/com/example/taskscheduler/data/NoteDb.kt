package com.example.taskscheduler.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.taskscheduler.domain.CheckNoteItem
import com.example.taskscheduler.domain.UrgencyOfNote
import com.example.taskscheduler.domain.User

@Entity(tableName = "notes")
data class NoteDb(
    @PrimaryKey
    val id: String,
    val title: String,
    val creatorId: String,
    val members: List<User>,
    val description: String,
    val date: String,
    val listOfTasks: List<CheckNoteItem>,
    val priority: UrgencyOfNote
)
