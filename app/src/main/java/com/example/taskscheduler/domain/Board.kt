package com.example.taskscheduler.domain

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "boards")
data class Board(
    @PrimaryKey
    val id: String = "",
    var title: String = "",
    val creatorId: String = "",
    val backgroundUrl: String = "",
    val members: List<String> = emptyList(),
    var listsOfNotesIds: List<String> = emptyList()

    ) :
    Parcelable