package com.example.taskscheduler.data.mappers

import com.example.taskscheduler.data.entities.ListOfNotesEntity
import com.example.taskscheduler.domain.models.ListOfNotesItem

class ListOfNotesItemToListOfNotesEntityMapper {

    fun map(listOfNotesItem: ListOfNotesItem): ListOfNotesEntity {
        return ListOfNotesEntity(
            id = listOfNotesItem.id,
            title = listOfNotesItem.title,
            creatorId = listOfNotesItem.creatorId,
            listNotes = listOfNotesItem.listNotes
        )
    }

}