package com.example.taskscheduler.data.mappers

interface Mapper<in I, out O> {

    fun map(from: I): O
}