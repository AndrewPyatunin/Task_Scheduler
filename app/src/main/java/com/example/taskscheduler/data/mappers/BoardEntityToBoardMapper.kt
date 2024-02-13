package com.example.taskscheduler.data.mappers

import com.example.taskscheduler.data.entities.BoardEntity
import com.example.taskscheduler.domain.models.Board

class BoardEntityToBoardMapper : Mapper<BoardEntity?, Board> {

    override fun map(from: BoardEntity?): Board {
        return Board(
            id = from?.id ?: "",
            title = from?.title ?: "",
            creatorId = from?.creatorId ?: "",
            backgroundUrl = from?.backgroundUrl ?: "",
            members = from?.members ?: emptyMap(),
            listsOfNotesIds = from?.listOfNotesIds ?: emptyMap()
        )
    }
}