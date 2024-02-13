package com.example.taskscheduler.data.mappers

import com.example.taskscheduler.data.entities.NotesListEntity
import com.example.taskscheduler.domain.models.NotesListItem

class NotesListEntityToNotesListItemMapper : Mapper<NotesListEntity?, NotesListItem> {

    override fun map(from: NotesListEntity?): NotesListItem {
        return NotesListItem(
            id = from?.id ?: "",
            title = from?.title ?: "",
            creatorId = from?.creatorId ?: "",
            listNotes = from?.listNotes ?: emptyMap()
        )
    }
}