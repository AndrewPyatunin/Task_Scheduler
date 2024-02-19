package com.example.taskscheduler

import android.app.Application
import com.example.taskscheduler.data.database.*
import com.example.taskscheduler.di.DaggerAppComponent

class MyApp : Application() {

    val component by lazy { DaggerAppComponent.factory().create(this) }

    companion object {

        fun clear (application: Application) {
            synchronized(this) {
                TaskDatabase.getInstance(application).clearAllTables()
            }
        }
    }
}