package com.example.taskscheduler.domain.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("lastName")
    val lastName: String = "",
    @SerializedName("email")
    val email: String = "",
    @SerializedName("onlineStatus")
    val onlineStatus: Boolean = false,
    @SerializedName("boards")
    val boards: Map<String, Boolean> = emptyMap(),
    @SerializedName("uri")
    val uri: String = "",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("invites")
    val invites: Map<String, Boolean> = emptyMap()

) : Parcelable