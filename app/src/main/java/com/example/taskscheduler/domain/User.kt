package com.example.taskscheduler.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: String = "",
    val name: String = "",
    val lastName: String = "",
    val email: String = "",
    var onlineStatus: Boolean = false,
    var boards: List<String> = emptyList()
) :
    Parcelable