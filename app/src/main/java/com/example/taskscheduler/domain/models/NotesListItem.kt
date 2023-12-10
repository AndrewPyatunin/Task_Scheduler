package com.example.taskscheduler.domain.models

import android.os.Parcelable
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotesListItem(
    @PrimaryKey
    val id: String = "",
    val title: String = "",
    val creatorId: String = "",
    val listNotes: Map<String, Boolean> = emptyMap()
) : Parcelable