package com.example.taskscheduler.domain.models

import android.os.Parcelable
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class Board(
    @PrimaryKey
    val id: String = "",
    val title: String = "",
    val creatorId: String = "",
    val backgroundUrl: String = "",
    val members: List<String> = emptyList(),
    val listsOfNotesIds: List<String> = emptyList()
) : Parcelable