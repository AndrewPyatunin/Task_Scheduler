package com.example.taskscheduler.data.mappers

interface Mapper <I, O> {

    fun map(from: I): O
}