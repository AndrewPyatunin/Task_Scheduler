package com.example.taskscheduler.domain.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotesListItem(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("title")
    val title: String = "",
    @SerializedName("creatorId")
    val creatorId: String = "",
    @SerializedName("listNotes")
    val listNotes: Map<String, Boolean> = emptyMap()
) : Parcelable