package com.example.taskscheduler

import android.app.Application
import com.example.taskscheduler.data.database.*

class MyApp : Application() {

    companion object {

        fun clear (application: Application) {
            synchronized(this) {
                TaskDatabase.getInstance(application).clearAllTables()
            }
        }
    }
}