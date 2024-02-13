package com.example.taskscheduler.domain.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Board(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("title")
    val title: String = "",
    @SerializedName("creatorId")
    val creatorId: String = "",
    @SerializedName("backgroundUrl")
    val backgroundUrl: String = "",
    @SerializedName("members")
    val members: Map<String, Boolean> = emptyMap(),
    @SerializedName("listsOfNotesIds")
    val listsOfNotesIds: Map<String, Boolean> = emptyMap()
) : Parcelable