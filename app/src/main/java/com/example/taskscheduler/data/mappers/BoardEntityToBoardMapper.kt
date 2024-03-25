package com.example.taskscheduler.data.mappers

import com.example.taskscheduler.data.entities.BoardEntity
import com.example.taskscheduler.domain.models.Board
import javax.inject.Inject

class BoardEntityToBoardMapper @Inject constructor() : Mapper<BoardEntity?, Board> {

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