package com.example.taskscheduler.data

import com.example.taskscheduler.domain.Note

class MapperForNoteAndNoteDb {

    fun map(noteDb: NoteDb): Note {
        return Note(
            id = noteDb.id,
            title = noteDb.title,
            creatorId = noteDb.creatorId,
            members = noteDb.members,
            description = noteDb.description,
            date = noteDb.date,
            listOfTasks = noteDb.listOfTasks,
            priority = noteDb.priority
        )
    }

    fun mapList(list: List<NoteDb>): List<Note> {
        return list.map { map(it) }
    }

    fun mapToDb(note: Note): NoteDb {
        return NoteDb(
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

    fun mapListToDb(list: List<Note>): List<NoteDb> {
        return list.map { mapToDb(it) }
    }
}