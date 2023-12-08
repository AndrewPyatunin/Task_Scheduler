package com.example.taskscheduler.data.mappers

import com.example.taskscheduler.data.entities.NoteEntity
import com.example.taskscheduler.domain.models.Note

class NoteToNoteEntityMapper : Mapper<Note, NoteEntity> {

    override fun map(from: Note): NoteEntity {
        return NoteEntity(
            id = from.id,
            title = from.title,
            creatorId = from.creatorId,
            members = from.members,
            description = from.description,
            date = from.date,
            listOfTasks = from.listOfTasks,
            priority = from.priority
        )
    }
}