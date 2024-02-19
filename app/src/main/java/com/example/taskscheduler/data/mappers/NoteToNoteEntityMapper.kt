package com.example.taskscheduler.data.mappers

import com.example.taskscheduler.data.entities.CheckNoteEntity
import com.example.taskscheduler.data.entities.NoteEntity
import com.example.taskscheduler.domain.models.CheckNoteItem
import com.example.taskscheduler.domain.models.Note
import javax.inject.Inject

class NoteToNoteEntityMapper @Inject constructor(private val mapper: Mapper<CheckNoteItem, CheckNoteEntity>) : Mapper<Note, NoteEntity> {

    override fun map(from: Note): NoteEntity {
        return NoteEntity(
            id = from.id,
            title = from.title,
            creatorId = from.creatorId,
            members = from.members,
            description = from.description,
            date = from.date,
            listOfTasks = from.listOfTasks.map { mapper.map(it) },
            priority = from.priority
        )
    }
}