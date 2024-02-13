package com.example.taskscheduler.presentation.boardupdated

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.DiffCallback
import com.example.taskscheduler.domain.models.NotesListItem
import com.example.taskscheduler.domain.models.User

class OuterBoardAdapter(
    lifecycle: Lifecycle,
    fragmentManager: FragmentManager,
    list: ArrayList<NotesListItem> = ArrayList(),
    private val board: Board,
    private val user: User,
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    var fragmentList = list//not empty
        set(value) {
            val diffCallback = DiffCallback(field, value)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            diffResult.dispatchUpdatesTo(this)
            field = value
        }

    override fun getItemCount(): Int = fragmentList.size

    override fun createFragment(position: Int): Fragment {
        return InnerBoardFragment.newInstance(fragmentList, position, board, user)
    }
}