package com.example.taskscheduler.domain.models

import android.os.Parcelable
import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Invite(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("boardId")
    val boardId: String = "",
    @SerializedName("userSenderId")
    val userSenderId: String = "",
    @SerializedName("userName")
    val userName: String = "",
    @SerializedName("userLastName")
    val userLastName: String = "",
    @SerializedName("boardName")
    val boardName: String = ""
): Parcelable
