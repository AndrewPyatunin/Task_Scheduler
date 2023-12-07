package com.example.taskscheduler.data.mappers

import com.example.taskscheduler.data.entities.BoardEntity
import com.example.taskscheduler.domain.models.Board

class BoardToBoardEntityMapper {

    fun map(board: Board): BoardEntity {
        return BoardEntity(
            id = board.id,
            title = board.title,
            creatorId = board.creatorId,
            backgroundUrl = board.backgroundUrl,
            members = board.members,
            listOfNotesIds = board.listsOfNotesIds
        )
    }

}