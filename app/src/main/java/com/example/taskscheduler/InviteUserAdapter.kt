package com.example.taskscheduler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.taskscheduler.domain.User

class UserListDiffCallback(
    private val oldList: List<User>,
    private val newList: List<User>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldUser = oldList[oldItemPosition]
        val newUser = newList[newItemPosition]
        return oldUser.id == newUser.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldUser = oldList[oldItemPosition]
        val newUser = newList[newItemPosition]
        return oldUser == newUser
    }

}

class InviteUserAdapter : Adapter<InviteUserAdapter.InviteUserViewHolder>() {
    var onItemClick: ((User) -> Unit)? = null
    var users = ArrayList<User>()
    set(newValue) {
        val diffCallback = UserListDiffCallback(field, newValue)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        diffResult.dispatchUpdatesTo(this)
        field = newValue
    }

    inner class InviteUserViewHolder
        (itemView: View,
         var textViewUserInfo: TextView = itemView.findViewById(R.id.textViewUserInfo),
         var checkBoxInvited: CheckBox = itemView.findViewById(R.id.checkBoxInvited)
    ): ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(users[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InviteUserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return InviteUserViewHolder(view)
    }

    override fun onBindViewHolder(holder: InviteUserViewHolder, position: Int) {
        val user = users[position]
        val userInfo = String.format("%s %s", user.name, user.lastName)
//        holder.checkBoxInvited.onTouchEvent()
        holder.textViewUserInfo.text = userInfo
    }

    override fun getItemCount(): Int = users.size
}