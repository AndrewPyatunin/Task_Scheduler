package com.example.taskscheduler.data.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.taskscheduler.data.entities.*

@Database(
    entities = [BoardEntity::class, NoteEntity::class, NotesListEntity::class, UserEntity::class, InviteEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TaskDatabase : RoomDatabase() {

    companion object {

        private var instance: TaskDatabase? = null
        private val LOCK = Any()
        private const val DB_NAME = "taskDatabase.db"

        fun getInstance(application: Application): TaskDatabase {
            instance?.let {
                return it
            }
            synchronized(LOCK) {
                instance?.let {
                    return it
                }
                instance =
                    Room.databaseBuilder(application, TaskDatabase::class.java, DB_NAME).build()
                return instance as TaskDatabase
            }
        }
    }

    abstract fun taskDatabaseDao(): TaskDatabaseDao
}