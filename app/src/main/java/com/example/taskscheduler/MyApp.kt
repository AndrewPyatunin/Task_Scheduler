package com.example.taskscheduler

import android.app.Application
import com.example.taskscheduler.data.database.TaskDatabase
import com.example.taskscheduler.data.database.TaskDatabaseDao
import com.example.taskscheduler.data.repos.*

class MyApp : Application() {

    companion object {

        private lateinit var dao: TaskDatabaseDao

        val boardRepository by lazy {
            BoardRepositoryImpl(dao)
        }
        val noteRepository by lazy {
            NoteRepositoryImpl(dao)
        }
        val notesListRepository by lazy {
            NotesListRepositoryImpl(dao)
        }
        val userRepository by lazy {
            UserRepositoryImpl(dao)
        }
        val userAuthentication by lazy {
            UserAuthentication(dao)
        }
        val inviteRepository by lazy {
            InviteRepositoryImpl(dao)
        }

        fun initialize (application: Application) {
            synchronized(this) {
                dao = TaskDatabase.getInstance(application).taskDatabaseDao()
            }
        }

        fun clear (application: Application) {
            synchronized(this) {
                TaskDatabase.getInstance(application).clearAllTables()
            }
        }
    }
}