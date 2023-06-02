package com.example.taskscheduler.presentation.boardupdated

import android.util.Log
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import androidx.viewpager2.widget.ViewPager2
import com.example.taskscheduler.domain.Board
import com.example.taskscheduler.domain.DiffCallback
import com.example.taskscheduler.domain.ListOfNotesItem
import com.example.taskscheduler.domain.User
import com.google.android.material.tabs.TabLayout

class OuterBoardAdapter(
    lifecycle: Lifecycle,
    fragmentManager: FragmentManager,
//    fragment: Fragment,
    val board: Board,
    val user: User,
    var position: Int,
    var tabLayout: TabLayout,
    var viewPager2: ViewPager2,
    list: ArrayList<ListOfNotesItem>,
    var isFirst: Boolean
    ) : FragmentStateAdapter(fragmentManager, lifecycle) {
        var fragmentList = list
            set(value) {
                val diffCallback = DiffCallback(field, value)
                val diffResult = DiffUtil.calculateDiff(diffCallback)
                diffResult.dispatchUpdatesTo(this)
                field = value
            }

        override fun getItemCount(): Int = fragmentList.size

//    fun newInstance(position: Int, isFirst: Boolean) {
//        this.position = position
//        this.isFirst = isFirst
//    }

        override fun createFragment(position: Int): Fragment {
//            return fragmentList[position]
//            if (isFirst) {
//                Log.i("USER_IS_FIRST", isFirst.toString())
//                isFirst = false
//                tabLayout.getTabAt(this.position)?.select()
//                return InnerBoardFragment.newInstance(fragmentList[this.position], board, user)
//            }
//            return InnerBoardFragment.newInstance(fragmentList[position], board, user)

            if (this.position != 0) {
                val position = this.position
                this.position = 0
                viewPager2.currentItem = position
                tabLayout.getTabAt(position)?.select()
//                return InnerBoardFragment.newInstance(fragmentList[position], board, user)

            }
            return InnerBoardFragment.newInstance(fragmentList[position], board, user)
        }
}