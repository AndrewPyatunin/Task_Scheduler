package com.example.taskscheduler.domain

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import com.example.taskscheduler.domain.models.*

class DiffCallback<T>(
    private val oldList: List<T>,
    private val newList: List<T>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val old = oldList[oldItemPosition]
        val new = newList[newItemPosition]
        val oldId = when (old) {
            is BackgroundImage -> old.id
            is Fragment -> old.id
            is Note -> old.id
            is Board -> old.id
            is CheckNoteItem -> old.id
            is Invite -> old.id
            is ListOfBoards -> old.id
            is NotesListItem -> old.id
            is User -> old.id
            else -> ""
        }
        val newId = when (new) {
            is BackgroundImage -> new.id
            is Fragment -> new.id
            is Note -> new.id
            is Board -> new.id
            is CheckNoteItem -> new.id
            is Invite -> new.id
            is ListOfBoards -> new.id
            is NotesListItem -> new.id
            is User -> new.id
            else -> ""
        }
        return oldId == newId
    }
}