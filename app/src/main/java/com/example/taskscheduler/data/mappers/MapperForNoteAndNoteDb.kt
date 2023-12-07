package com.example.taskscheduler.data.mappers

import com.example.taskscheduler.data.entities.NoteEntity
import com.example.taskscheduler.domain.models.Note

class MapperForNoteAndNoteDb {

    fun map(noteEntity: NoteEntity): Note {
        return Note(
            id = noteEntity.id,
            title = noteEntity.title,
            creatorId = noteEntity.creatorId,
            members = noteEntity.members,
            description = noteEntity.description,
            date = noteEntity.date,
            listOfTasks = noteEntity.listOfTasks,
            priority = noteEntity.priority
        )
    }

    fun mapList(list: List<NoteEntity>): List<Note> {
        return list.map { map(it) }
    }

    fun mapToDb(note: Note): NoteEntity {
        return NoteEntity(
            id = note.id,
            title = note.title,
            creatorId = note.creatorId,
            members = note.members,
            description = note.description,
            date = note.date,
            listOfTasks = note.listOfTasks,
            priority = note.priority
        )
    }

    fun mapListToDb(list: List<Note>): List<NoteEntity> {
        return list.map { mapToDb(it) }
    }
}