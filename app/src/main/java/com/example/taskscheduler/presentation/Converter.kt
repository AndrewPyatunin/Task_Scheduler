package com.example.taskscheduler.presentation

import com.example.taskscheduler.domain.BackgroundImage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Converter {

    fun fromStringToList(value: String?): List<BackgroundImage> {
        if (value == null) return emptyList()
        return Gson().fromJson(value, object : TypeToken<List<BackgroundImage>>() {}.type)
    }

    fun fromListToString(list: List<BackgroundImage?>?): String {
        return Gson().toJson(list)
    }
}