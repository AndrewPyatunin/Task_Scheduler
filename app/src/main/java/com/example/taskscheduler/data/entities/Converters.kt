package com.example.taskscheduler.data.entities

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class   Converters {

    @TypeConverter
    fun fromString(value: String?): List<String> {
        if (value == null) return emptyList()

        return Gson().fromJson(value, object : TypeToken<List<String>>() {}.type)
    }

    @TypeConverter
    fun fromListString(list: List<String?>?): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun fromStringToListUser(value: String?): List<UserEntity> {
        if (value == null) return emptyList()

        return Gson().fromJson(value, object : TypeToken<List<UserEntity>>() {}.type)
    }

    @TypeConverter
    fun fromListUserEntityToString(list: List<UserEntity?>?): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun fromStringToListCheckNoteEntity(value: String?): List<CheckNoteEntity> {
        if (value == null) return emptyList()

        return Gson().fromJson(value, object : TypeToken<List<CheckNoteEntity>>() {}.type)
    }

    @TypeConverter
    fun fromListCheckNoteItemToString(list: List<CheckNoteEntity?>?): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun fromStringToMapStringBoolean(value: String?): Map<String, Boolean> {
        if (value == null) return emptyMap()

        return Gson().fromJson(value, object : TypeToken<Map<String, Boolean>>() {}.type)
    }

    @TypeConverter
    fun fromMapStringBooleanToString(map: Map<String?, Boolean?>?): String {
        return Gson().toJson(map)
    }
}