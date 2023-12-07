package com.example.taskscheduler.domain.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class ListOfNotesItem(
    @PrimaryKey
    val id: String = "",
    val title: String = "",
    val creatorId: String = "",
    val listNotes: Map<String, Boolean> = HashMap()
    ) : Parcelable