package com.example.taskscheduler.data.mappers

import com.example.taskscheduler.data.entities.CheckNoteEntity
import com.example.taskscheduler.domain.models.CheckNoteItem
import javax.inject.Inject

class CheckNoteEntityToCheckNoteItemMapper @Inject constructor() : Mapper<CheckNoteEntity?, CheckNoteItem> {

    override fun map(from: CheckNoteEntity?): CheckNoteItem {
        return CheckNoteItem(
            itemTitle = from?.itemTitle ?: "",
            isChecked = from?.isChecked ?: false,
            id = from?.id ?: ""
        )
    }
}