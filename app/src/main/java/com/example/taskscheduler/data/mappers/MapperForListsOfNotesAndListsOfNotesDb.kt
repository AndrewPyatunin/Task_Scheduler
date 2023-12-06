package com.example.taskscheduler.data.mappers

import com.example.taskscheduler.data.modelsDb.ListOfNotesDb
import com.example.taskscheduler.domain.models.ListOfNotesItem

class MapperForListsOfNotesAndListsOfNotesDb {
    fun map(listOfNotesDb: ListOfNotesDb): ListOfNotesItem {
        return ListOfNotesItem(
            id = listOfNotesDb.id,
            title = listOfNotesDb.title,
            creatorId = listOfNotesDb.creatorId,
            listNotes = listOfNotesDb.listNotes
        )
    }

    fun mapList(list: List<ListOfNotesDb>): List<ListOfNotesItem> {
        return list.map { map(it) }
    }

    fun mapToDb(listOfNotesItem: ListOfNotesItem): ListOfNotesDb {
        return ListOfNotesDb(
            id = listOfNotesItem.id,
            title = listOfNotesItem.title,
            creatorId = listOfNotesItem.creatorId,
            listNotes = listOfNotesItem.listNotes
        )
    }

    fun mapToDbList(list: List<ListOfNotesItem>): List<ListOfNotesDb> {
        return list.map { mapToDb(it) }
    }
}