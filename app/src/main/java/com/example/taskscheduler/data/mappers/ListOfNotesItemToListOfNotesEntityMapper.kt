package com.example.taskscheduler.data.mappers

import com.example.taskscheduler.data.entities.ListOfNotesEntity
import com.example.taskscheduler.domain.models.ListOfNotesItem

class ListOfNotesItemToListOfNotesEntityMapper : Mapper<ListOfNotesItem, ListOfNotesEntity> {

    override fun map(from: ListOfNotesItem): ListOfNotesEntity {
        return ListOfNotesEntity(
            id = from.id,
            title = from.title,
            creatorId = from.creatorId,
            listNotes = from.listNotes
        )
    }

}