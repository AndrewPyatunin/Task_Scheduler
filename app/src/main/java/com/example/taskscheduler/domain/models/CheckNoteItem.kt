package com.example.taskscheduler.domain.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CheckNoteItem(
    @SerializedName("itemTitle")
    val itemTitle: String = "",
    @SerializedName("isChecked")
    @field:JvmField
    val isChecked: Boolean = false,
    @SerializedName("id")
    val id: String = ""
): Parcelable