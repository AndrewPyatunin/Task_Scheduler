package com.example.taskscheduler.data.entities

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    private inline fun <reified T> Gson.fromJson(json: String?) =
        fromJson<T>(json, object : TypeToken<List<T>>() {}.type)


    @TypeConverter
    fun fromString(value: String?): List<String> {
        return Gson().fromJson(value)
    }

    @TypeConverter
    fun fromListString(list: List<String?>?): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun fromStringToListUser(value: String?): List<UserEntity> {
        if (value == null) return emptyList()

        return Gson().fromJson<List<UserEntity>>(value)
    }

    @TypeConverter
    fun fromListUserEntityToString(list: List<UserEntity?>?): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun fromStringToListCheckNoteEntity(value: String?): List<CheckNoteEntity> {
        if (value == null) return emptyList()

        return Gson().fromJson<List<CheckNoteEntity>>(value)
    }

    @TypeConverter
    fun fromListCheckNoteItemToString(list: List<CheckNoteEntity>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun fromStringToMapStringBoolean(value: String?): Map<String, Boolean> {
        if (value == null) return emptyMap()

        return Gson().fromJson<Map<String, Boolean>>(value)
    }

    @TypeConverter
    fun fromMapStringBooleanToString(map: Map<String, Boolean>): String {
        return Gson().toJson(map)
    }
}