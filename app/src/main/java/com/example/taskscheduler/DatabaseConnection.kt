package com.example.taskscheduler

import android.app.Application
import com.example.taskscheduler.domain.BackgroundImage
import com.example.taskscheduler.domain.NewCallback
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope

interface DatabaseConnection {

    suspend fun query(application: Application, scope: CoroutineScope)

    fun getBackgroundImages(callback: NewCallback)
}