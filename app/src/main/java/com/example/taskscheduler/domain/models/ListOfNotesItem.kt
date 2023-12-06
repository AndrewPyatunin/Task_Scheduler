package com.example.taskscheduler.domain.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "listsOfNotes")
data class ListOfNotesItem(
    @PrimaryKey
    var id: String = "",
    val title: String = "",
    val creatorId: String = "",
    var listNotes: Map<String, Boolean> = HashMap()
    ) : Parcelable