package com.example.taskscheduler.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CheckNoteItem(
    val itemTitle: String = "",
    val isChecked: Boolean = false,
    val id: String = ""
): Parcelable