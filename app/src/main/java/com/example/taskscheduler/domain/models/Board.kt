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
    val members: Map<String, Boolean> = emptyMap(),
    val listsOfNotesIds: Map<String, Boolean> = emptyMap()
) : Parcelable